package io.codelabs.devtools.tokenizer;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Tokenizer {

    public static void main(String[] args) {

        String test = "${${test1}${test2}}${test3}${test4}${${${test5}}}${test6}--${${test1}${test2}}${test3}${test4}${${${test5}}}${test6}";

        long time = measureNanoTime(() -> {
            try {
                tokenizer(test).forEach(System.out::println);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.printf("Execution time: %s ns (~%s ms)%n", time, (double) time / 1000000);
    }

    private static List<String> tokenizer(String input) throws Exception {
        List<String> result = new ArrayList<>();
        Stack<Integer> nestedExpressionStack = new Stack<>();
        char[] chars = input.toCharArray();
        int start = 0;
        int current = 0;

        while (current < chars.length) {

            if (chars[current] == '$' && current < chars.length - 1 && chars[current + 1] == '{') {
                int expressionStart = current;
                if (start != current) result.add(input.substring(start, current));
                current++;

                while (current < chars.length) {
                    if (chars[current] == '$' && current < chars.length - 1 && chars[current + 1] == '{') {
                        nestedExpressionStack.push(0);
                    } else if (chars[current] == '}') {
                        if (nestedExpressionStack.empty()) {
                            current++;
                            result.add(input.substring(expressionStart, current));
                            start = current;
                            break;
                        } else {
                            nestedExpressionStack.pop();
                        }
                    }

                    current++;
                    if (current == chars.length) {
                        throw new Exception("Invalid format");
                    }
                }

            } else {
                current++;
            }
        }

        if (current > start) result.add(input.substring(start, current));

        return result;
    }

    private static long measureNanoTime(Runnable runnable) {
        long start = System.nanoTime();
        runnable.run();
        return System.nanoTime() - start;
    }
}
