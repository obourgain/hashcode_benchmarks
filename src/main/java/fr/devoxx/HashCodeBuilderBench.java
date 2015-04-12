/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package fr.devoxx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@Warmup(time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 10, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class HashCodeBuilderBench {

    private static final String PERSONS_FILE = "names.txt";

    private static List<Person> persons;
    private static List<PersonWithHashCodeBuilder> personsWithHashCodeBuilders;
    private static List<PersonWithHashCodeBuilder2> personsWithHashCodeBuilders2;
    private static List<PersonWithReflectionHashCode> personsWithReflectionHashCodes;

    static {
        persons = loadPersonsFromFile(Person::new);
        personsWithHashCodeBuilders = loadPersonsFromFile(PersonWithHashCodeBuilder::new);
        personsWithHashCodeBuilders2 = loadPersonsFromFile(PersonWithHashCodeBuilder2::new);
        personsWithReflectionHashCodes = loadPersonsFromFile(PersonWithReflectionHashCode::new);
    }

    @Benchmark
    public void simpleHashCode(Blackhole blackhole) {
        HashSet<Person> set = new HashSet<>(persons.size());
        for (Person person : persons) {
            set.add(person);
        }
        blackhole.consume(set);
    }

    @Benchmark
    public void hashCodeBuilder(Blackhole blackhole) {
        HashSet<PersonWithHashCodeBuilder> set = new HashSet<>(personsWithHashCodeBuilders.size());
        for (PersonWithHashCodeBuilder person : personsWithHashCodeBuilders) {
            set.add(person);
        }
        blackhole.consume(set);
    }

    @Benchmark
    public void hashCodeBuilder2(Blackhole blackhole) {
        HashSet<PersonWithHashCodeBuilder2> set = new HashSet<>(personsWithHashCodeBuilders2.size());
        for (PersonWithHashCodeBuilder2 person : personsWithHashCodeBuilders2) {
            set.add(person);
        }
        blackhole.consume(set);
    }

    @Benchmark
    public void reflectionHashCode(Blackhole blackhole) {
        HashSet<PersonWithReflectionHashCode> set = new HashSet<>(personsWithReflectionHashCodes.size());
        for (PersonWithReflectionHashCode person : personsWithReflectionHashCodes) {
            set.add(person);
        }
        blackhole.consume(set);
    }

    private static <T> List<T> loadPersonsFromFile(Function<String, T> map) {
        try {
            return Files.readAllLines(Paths.get(Thread.currentThread().getContextClassLoader().getResource(PERSONS_FILE).getFile()))
                    .stream()
                    .map(map)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class Person {

        private final String name;

        public Person(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return !(name != null ? !name.equals(person.name) : person.name != null);
        }

        @Override
        public int hashCode() {
            return name == null ? 0 : name.hashCode();
        }
    }

    static class PersonWithHashCodeBuilder {

        private final String name;

        public PersonWithHashCodeBuilder(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return !(name != null ? !name.equals(person.name) : person.name != null);
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .append(name)
                    .build();
        }
    }

    static class PersonWithHashCodeBuilder2 {

        private final String name;

        public PersonWithHashCodeBuilder2(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return !(name != null ? !name.equals(person.name) : person.name != null);
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(name).build();
        }
    }

    static class PersonWithReflectionHashCode {

        private final String name;

        public PersonWithReflectionHashCode(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return !(name != null ? !name.equals(person.name) : person.name != null);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + HashCodeBuilderBench.class.getSimpleName() + ".*")
                .shouldDoGC(true)
                .warmupIterations(10)
                .measurementIterations(10)
                .jvmArgs("-Xmx128m", "-Xms128m", "-XX:+DoEscapeAnalysis")
//                .warmupTime(TimeValue.seconds(5))
//                .measurementTime(TimeValue.seconds(5))
//                .mode(Mode.AverageTime)
//                .timeUnit(TimeUnit.MICROSECONDS)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
