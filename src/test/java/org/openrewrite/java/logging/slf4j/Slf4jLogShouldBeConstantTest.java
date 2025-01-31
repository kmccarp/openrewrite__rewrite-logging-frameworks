/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.logging.slf4j;

import org.junit.jupiter.api.Test;
import org.openrewrite.Issue;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

@SuppressWarnings("EmptyTryBlock")
class Slf4jLogShouldBeConstantTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new Slf4jLogShouldBeConstant())
          .parser(JavaParser.fromJavaVersion().classpath("slf4j-api"));
    }

    @Issue("https://github.com/openrewrite/rewrite-logging-frameworks/issues/41")
    @Test
    void doNotUseStringFormat() {
        //language=java
        rewriteRun(
          java(
            """
              import org.slf4j.Logger;
              class Test {
                  Logger logger;
                  void test() {
                      Object obj1 = new Object();
                      Object obj2 = new Object();
                      logger.info(String.format("Object 1 is %s and Object 2 is %s", obj1, obj2));
                  }
              }
              """,
            """
              import org.slf4j.Logger;
              class Test {
                  Logger logger;
                  void test() {
                      Object obj1 = new Object();
                      Object obj2 = new Object();
                      logger.info("Object 1 is {} and Object 2 is {}", obj1, obj2);
                  }
              }
              """
          )
        );
    }

    @Issue("https://github.com/openrewrite/rewrite-logging-frameworks/issues/41")
    @Test
    void doNotUseValueOfException() {
        //language=java
        rewriteRun(
          java(
            """
              import org.slf4j.Logger;
              class Test {
                  Logger logger;
                  void test() {
                      try {
                      } catch(Exception e) {
                          logger.warn(String.valueOf(e));
                      }
                  }
              }
              """,
            """
              import org.slf4j.Logger;
              class Test {
                  Logger logger;
                  void test() {
                      try {
                      } catch(Exception e) {
                          logger.warn("Exception", e);
                      }
                  }
              }
              """
          )
        );
    }

    @Issue("https://github.com/openrewrite/rewrite-logging-frameworks/issues/41")
    @Test
    void doNotUseToString() {
        //language=java
        rewriteRun(
          java(
            """
              import org.slf4j.Logger;
              class Test {
                  Logger logger;
                  void test() {
                      Object obj1 = new Object();
                      logger.info(obj1.toString());
                  }
              }
              """,
            """
              import org.slf4j.Logger;
              class Test {
                  Logger logger;
                  void test() {
                      Object obj1 = new Object();
                      logger.info("{}", obj1);
                  }
              }
              """
          )
        );
    }
}
