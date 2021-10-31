package info.kgeorgiy.ja.strelnikov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {
    private final ConcurrentHashMap<String, HostQueue> hostQueueMap;
    private final ExecutorService downloaderService;
    private final ExecutorService parserService;
    private final Downloader downloader;
    private final int perHost;

    /**
     * Public constructor for WebCrawler
     *
     * @param downloader  download device
     * @param downloaders max number of downloaders
     * @param extractors  max number of extractors
     * @param perHost     max number of getting to one host
     */
    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.perHost = perHost;
        downloaderService = Executors.newFixedThreadPool(downloaders);
        parserService = Executors.newFixedThreadPool(extractors);
        hostQueueMap = new ConcurrentHashMap<>();
    }

    private class BFSCrawler {
        private final Map<String, IOException> exceptions;
        private final Queue<URLWrap> urls;
        private final Set<String> parsed;
        private final Set<String> downloaded;
        private final Phaser phaser;
        private final int depth;

        BFSCrawler(String url, int depth) {
            this.depth = depth;
            exceptions = new ConcurrentHashMap<>();
            urls = new ConcurrentLinkedQueue<>();
            parsed = ConcurrentHashMap.newKeySet();
            downloaded = ConcurrentHashMap.newKeySet();
            phaser = new Phaser(1);
            parsed.add(url);
            urls.add(new URLWrap(url, 1));
        }

        private void submitToParse(Document document, URLWrap currentUrl) {
            parserService.submit(() -> {
                try {
                    document.extractLinks()
                            .stream()
                            .filter(parsed::add)
                            .forEach(element -> urls.add(new URLWrap(element, currentUrl.getDepth() + 1)));
                } catch (IOException ignored) {
                } finally {
                    phaser.arriveAndDeregister();
                }
            });
        }

        private void submitToDownload(HostQueue currentHostQueue, URLWrap currentUrl) {
            phaser.register();
            currentHostQueue.addProcess(() -> {
                try {
                    Document document = downloader.download(currentUrl.getUrl());
                    downloaded.add(currentUrl.getUrl());
                    if (currentUrl.getDepth() < depth) {
                        phaser.register();
                        submitToParse(document, currentUrl);
                    }
                } catch (IOException e) {
                    exceptions.put(currentUrl.getUrl(), e);
                } finally {
                    phaser.arriveAndDeregister();
                    currentHostQueue.submitProcess();
                }
            });
        }

        private Result crawl() {
            while (!urls.isEmpty()) {
                if (urls.peek().getDepth() > phaser.getPhase()) {
                    phaser.arriveAndAwaitAdvance();
                }
                URLWrap currentUrl = urls.remove();
                try {
                    String host = URLUtils.getHost(currentUrl.getUrl());
                    HostQueue currentHostQueue = hostQueueMap.computeIfAbsent(
                            host,
                            element -> new HostQueue(downloaderService)
                    );
                    submitToDownload(currentHostQueue, currentUrl);
                } catch (MalformedURLException e) {
                    exceptions.put(currentUrl.getUrl(), e);
                }
                if (urls.isEmpty()) {
                    phaser.arriveAndAwaitAdvance();
                }
            }
            return new Result(new ArrayList<>(downloaded), exceptions);
        }
    }

    /**
     * Downloads web site up to specified depth.
     *
     * @param url   start <a href="http://tools.ietf.org/html/rfc3986">URL</a>.
     * @param depth download depth.
     * @return download result.
     */
    @Override
    public Result download(String url, int depth) {
        return (new BFSCrawler(url, depth).crawl());
    }

    /**
     * Closes this web-crawler, relinquishing any allocated resources.
     */
    @Override
    public void close() {
        downloaderService.shutdown();
        parserService.shutdown();
        try {
            boolean downloaded = downloaderService.awaitTermination(0, TimeUnit.SECONDS);
            boolean parsed = parserService.awaitTermination(0, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedException) {
            System.err.println("Termination is invalid.");
        }
    }

    private static int parse(String[] args, int index, int defaultVal) throws NumberFormatException {
        if (index >= args.length || args[index] == null) {
            return defaultVal;
        }
        int parsed = defaultVal;
        try {
            parsed = Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid value, index:" + index);
        }
        return parsed;
    }

    private static String getUrl(String[] args) {
        if (args == null) {
            System.err.println("Arguments should not be null.");
            throw new IllegalArgumentException();
        }
        if (args.length == 0) {
            System.err.println("Arguments should not be empty.");
            throw new IllegalArgumentException();
        }
        if (args[0] == null) {
            System.err.println("First argument should not be null.");
            throw new IllegalArgumentException();
        }
        return args[0];
    }

    public static void main(String[] args) {
        String url;
        try {
            url = getUrl(args);
        } catch (IllegalArgumentException e) {
            return;
        }
        int depth = parse(args, 1, 1);
        int downloads = parse(args, 2, 2);
        int extractors = parse(args, 3, 2);
        int perHost = parse(args, 4, 1);
        try (WebCrawler webCrawler = new WebCrawler(new CachingDownloader(), downloads, extractors, perHost)) {
            webCrawler.download(url, depth);
        } catch (IOException e) {
            System.out.println("Incorrect temporary repository creation for downloader.");
        }
    }

    private class   HostQueue {
        private final Queue<Runnable> runnableQueue;
        private final ExecutorService downloaderService;
        private int currentOccupation;

        HostQueue(ExecutorService downloaderService) {
            this.downloaderService = downloaderService;
            runnableQueue = new ArrayDeque<>();
            currentOccupation = 0;
        }

        public synchronized void addProcess(Runnable runnable) {
            if (currentOccupation < perHost) {
                currentOccupation++;
                downloaderService.submit(runnable);
            } else {
                runnableQueue.add(runnable);
            }
        }

        public synchronized void submitProcess() {
            if (runnableQueue.isEmpty()) {
                currentOccupation--;
            } else {
                downloaderService.submit(runnableQueue.poll());
            }
        }
    }

    private static class URLWrap {
        private final String url;
        private final int depth;

        public URLWrap(String url, int depth) {
            this.url = url;
            this.depth = depth;
        }

        public String getUrl() {
            return url;
        }

        public int getDepth() {
            return depth;
        }
    }
}
