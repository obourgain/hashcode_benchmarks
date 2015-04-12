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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
@Warmup(time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 10, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Threads(1)
@Fork(1)
public class HashMapResize {

    // values where the magic^wresize happens
    @Param({"24576", "24577", "196608", "196609", "3145728", "3145729"})
    public static int insertCount = 0;

    public static List<Integer> INTEGERS;

    @Setup
    public void prepare_boxed_ints() {
        INTEGERS = new ArrayList<>(insertCount);
        for (int i = 0; i < insertCount; i++) {
            INTEGERS.add(i);
        }
    }

    @Benchmark
    public void let_it_autoresize(Blackhole blackhole) throws NoSuchFieldException, IllegalAccessException {
        HashMap<Object, Object> map = new HashMap<>();
        for (int i = 0; i < INTEGERS.size(); i++) {
            Integer integer = INTEGERS.get(i);
            map.put(integer, integer);
        }
        blackhole.consume(map);
    }

    @Benchmark
    public void size_it_at_creation(Blackhole blackhole) throws NoSuchFieldException, IllegalAccessException {
        HashMap<Object, Object> map = new HashMap<>((int)(insertCount * 0.75f + 1));

        // to ensure the backing array size doesn't change
//        Field table = HashMap.class.getDeclaredField("table");
//        table.setAccessible(true);
//        map.put(1, 1); // init table
//        Object o1 = table.get(map);

        for (int i = 0; i < INTEGERS.size(); i++) {
            Integer integer = INTEGERS.get(i);
            map.put(integer, integer);
        }

//        Object o3 = table.get(map);
//
//        if(o1 != o3)
//        throw new RuntimeException(o1 + " "+ o3 + " ");

        blackhole.consume(map);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + HashMapResize.class.getSimpleName() + ".*")
                .shouldDoGC(true)
                .warmupIterations(10)
                .measurementIterations(10)
                .warmupTime(TimeValue.seconds(10))
                .measurementTime(TimeValue.seconds(10))
                .jvmArgs("-Xmx4g", "-Xms4g"/*, "-verbose:gc"*/)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
