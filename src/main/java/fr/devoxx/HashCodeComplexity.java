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
import java.util.stream.Collectors;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@Warmup(time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 10, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Threads(1)
@Fork(1)
@State(Scope.Benchmark)
public class HashCodeComplexity {

    /*

Benchmark                           (charsToHash)  Mode  Cnt      Score      Error  Units
HashCodeComplexity.charsInHashCode              0  avgt    5  12057,824 ± 3841,179  us/op
HashCodeComplexity.charsInHashCode              1  avgt    5    904,230 ±   46,861  us/op
HashCodeComplexity.charsInHashCode              2  avgt    5    260,614 ±   24,925  us/op
HashCodeComplexity.charsInHashCode              3  avgt    5    112,340 ±    7,881  us/op
HashCodeComplexity.charsInHashCode              4  avgt    5     75,002 ±    6,710  us/op
HashCodeComplexity.charsInHashCode              5  avgt    5     73,236 ±    7,159  us/op
HashCodeComplexity.charsInHashCode              6  avgt    5     61,946 ±    4,011  us/op

     */

    private static final String PERSONS_FILE = "names.txt";

    private static List<Person> persons = loadPersonsFromFile();

    /**
     * Nombre de chars à prendre en compte pour le calcul du hashCode
     */
    @Param({"0", "1", "2", "3", "4", "5", "6"})
    public static int charsToHash = 0;

    @Benchmark
    public void charsInHashCode(Blackhole blackhole) {
        HashSet<Person> set = new HashSet<>();
        for (Person person : persons) {
            set.add(person);
        }
        blackhole.consume(set);
    }

    private static List<Person> loadPersonsFromFile() {
        try {
            return Files.readAllLines(Paths.get(Thread.currentThread().getContextClassLoader().getResource(PERSONS_FILE).getFile()))
                    .stream()
                    .map(Person::new)
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
            return name == null ? 0 : name.substring(0, charsToHash).hashCode();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + HashCodeComplexity.class.getSimpleName() + ".*")
                .shouldDoGC(true)
                .warmupIterations(5)
                .measurementIterations(5)
                .warmupTime(TimeValue.seconds(10))
                .measurementTime(TimeValue.seconds(10))
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
