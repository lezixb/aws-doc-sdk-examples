// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[AssumeRole.java demonstrates how to obtain a set of temporary security credentials.]
// snippet-service:[AWS Security Token Service]
// snippet-keyword:[Java]
// snippet-keyword:[AWS Security Token Service]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-09-21]
// snippet-sourceauthor:[AWS-scmacdon]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

package com.example.sts;

// snippet-start:[sts.java2.assume_role.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
// snippet-end:[sts.java2.assume_role.import]

/**
 * To make this code example work, create a Role that you want to assume.
 * Then define a Trust Relationship in the AWS Console. You can use this as an example:
 *
 * {
 *   "Version": "2012-10-17",
 *   "Statement": [
 *     {
 *       "Effect": "Allow",
 *       "Principal": {
 *         "AWS": "<Specify the ARN of your IAM user you are using in this code example>"
 *       },
 *       "Action": "sts:AssumeRole"
 *     }
 *   ]
 * }
 *
 *  For more information, see "Editing the Trust Relationship for an Existing Role" in the AWS Directory Service guide.
 */
public class AssumeRole {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the role ARN value and the role session name.  \n" +
                "\n" +
                "Ex: AssumeRole <roleArn><roleSessionName>\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String roleArn = args[0];
        String roleSessionName = args[1];

        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
                .region(region)
                .build();

        assumeGivenRole(stsClient, roleArn, roleSessionName);
    }

    // snippet-start:[sts.java2.assume_role.main]
    public static void assumeGivenRole(StsClient stsClient, String roleArn, String roleSessionName) {

       try {
        AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName(roleSessionName)
                .build();

           AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
           Credentials myCreds = roleResponse.credentials();

           // Display the time when the temp creds expire
           Instant exTime = myCreds.expiration();
           String tokenInfo = myCreds.sessionToken();

           // Convert the Instant to readable date
           DateTimeFormatter formatter =
                   DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                           .withLocale( Locale.US)
                           .withZone( ZoneId.systemDefault() );

           formatter.format( exTime );
           System.out.println("The token "+tokenInfo + "  expires on " + exTime );

       } catch (StsException e) {
           System.err.println(e.getMessage());
           System.exit(1);
       }
   }
    // snippet-end:[sts.java2.assume_role.main]
}
