/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.exam.sample.karaf;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.inject.Inject;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.sample8.ds.Calculator;
import org.ops4j.pax.exam.testng.listener.PaxExam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(PaxExam.class)
public class CalculatorITest {

    private static Logger LOG = LoggerFactory.getLogger(CalculatorITest.class);

    @Inject
    protected Calculator calculator;

    @Configuration
    public Option[] config() {

        MavenArtifactUrlReference karafUrl = maven()
            .groupId("org.apache.karaf")
            .artifactId("apache-karaf")
            .version(karafVersion())
            .type("zip");

        MavenUrlReference karafStandardRepo = maven()
            .groupId("org.apache.karaf.features")
            .artifactId("standard")
            .version(karafVersion())
            .classifier("features")
            .type("xml");
        return new Option[] {
            mavenBundle("org.testng", "testng", "6.8.17"),
            mavenBundle("org.ops4j.pax.exam", "pax-exam-testng", "4.5.0-SNAPSHOT"),
            // KarafDistributionOption.debugConfiguration("5005", true),
            karafDistributionConfiguration()
                .frameworkUrl(karafUrl)
                .unpackDirectory(new File("target/exam"))
                .useDeployFolder(false),
            //keepRuntimeFolder(),
            KarafDistributionOption.features(karafStandardRepo, "scr"),
            mavenBundle()
                .groupId("org.ops4j.pax.exam.samples")
                .artifactId("pax-exam-sample8-ds")
                .versionAsInProject().start(),
                //junitBundles(),
       };
    }

    public static String karafVersion() {
        ConfigurationManager cm = new ConfigurationManager();
        String karafVersion = cm.getProperty("pax.exam.karaf.version", "3.0.0");
        return karafVersion;
    }


    @Test
    public void testAdd() {
        int result = calculator.add(1, 2);
        LOG.info("Result of add was {}", result);
        Assert.assertEquals(3, result);
    }

}
