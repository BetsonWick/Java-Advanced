package info.kgeorgiy.ja.strelnikov.student;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.GroupQuery;
import info.kgeorgiy.java.advanced.student.Student;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements GroupQuery {
    private final Comparator<? super Student> BASIC_COMPARATOR =
            Comparator.comparing(Student::getLastName).
                    thenComparing(Student::getFirstName).
                    reversed().thenComparing(Student::getId);

    private Map<GroupName, List<Student>> distributeStudentsToGroups(Collection<Student> collection, Comparator<? super Student> comparator) {
        return collection.stream().sorted(comparator).collect(Collectors.groupingBy(Student::getGroup));
    }

    private Stream<Map.Entry<GroupName, List<Student>>> getSortedStreamOfDistributing(Map<GroupName, List<Student>> distributing) {
        return distributing.entrySet().stream().sorted(Map.Entry.comparingByKey());
    }

    private List<Group> toListOfGroups(Stream<Map.Entry<GroupName, List<Student>>> stream) {
        return stream.map(a -> new Group(a.getKey(), a.getValue())).
                collect(Collectors.toList());
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> collection) {
        return toListOfGroups(getSortedStreamOfDistributing(distributeStudentsToGroups(collection,
                BASIC_COMPARATOR)));
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> collection) {
        return toListOfGroups(getSortedStreamOfDistributing(distributeStudentsToGroups(collection,
                Comparator.comparing(Student::getId))));
    }

    private GroupName getLargestByThen(Collection<Student> collection, Comparator<Map.Entry<GroupName, List<Student>>> then,
                                       boolean isDistinct) {
        return getSortedStreamOfDistributing(distributeStudentsToGroups(collection, BASIC_COMPARATOR)).
                max(Comparator.comparingInt((Map.Entry<GroupName, List<Student>> a) -> getSize(a.getValue(), isDistinct)).
                        thenComparing(then))
                .map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public GroupName getLargestGroup(Collection<Student> collection) {
        return getLargestByThen(collection, Map.Entry.comparingByKey(), false);
    }

    @Override
    public GroupName getLargestGroupFirstName(Collection<Student> collection) {
        return getLargestByThen(collection, Map.Entry.<GroupName, List<Student>>comparingByKey().reversed(), true);
    }

    private Integer getSize(List<Student> list, boolean distinct) {
        return distinct ? getDistinctFirstNames(list).size() : list.size();
    }

    public <A, E, C> C getStudentsField(Collection<Student> collection, Function<Student, A> function,
                                        Collector<A, E, C> collector) {
        return collection.stream().map(function).collect(collector);
    }

    @Override
    public List<String> getFirstNames(List<Student> list) {
        return getStudentsField(list, Student::getFirstName, Collectors.toList());
    }

    @Override
    public List<String> getLastNames(List<Student> list) {
        return getStudentsField(list, Student::getLastName, Collectors.toList());
    }

    @Override
    public List<GroupName> getGroups(List<Student> list) {
        return getStudentsField(list, Student::getGroup, Collectors.toList());
    }

    @Override
    public List<String> getFullNames(List<Student> list) {
        return getStudentsField(list, a -> a.getFirstName() + " " + a.getLastName(), Collectors.toList());
    }

    private List<Student> sortStudentsBy(Collection<Student> collection, Comparator<? super Student> comparator) {
        return collection.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> list) {
        return getStudentsField(list, Student::getFirstName, Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> list) {
        return list.stream().max(Comparator.comparingInt(Student::getId)).
                orElse(new Student(0, "", "", GroupName.M3237)).getFirstName();
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> collection) {
        return sortStudentsBy(collection, Comparator.comparing(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> collection) {
        return sortStudentsBy(collection, BASIC_COMPARATOR);
    }

    private List<Student> findStudentsBy(Collection<Student> collection, Predicate<? super Student> predicate) {
        return collection.stream().filter(predicate).sorted(BASIC_COMPARATOR).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> collection, String s) {
        return findStudentsBy(collection, a -> a.getFirstName().equals(s));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> collection, String s) {
        return findStudentsBy(collection, a -> a.getLastName().equals(s));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> collection, GroupName groupName) {
        return findStudentsBy(collection, a -> a.getGroup().equals(groupName));
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> collection, GroupName groupName) {
        return distributeStudentsToGroups(collection, BASIC_COMPARATOR).getOrDefault(groupName, new ArrayList<>()).stream().
                collect(Collectors.toMap(Student::getLastName, Student::getFirstName, (a, b) -> b));
    }
}
