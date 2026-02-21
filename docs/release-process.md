<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements. See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership. The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License. You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied. See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->

# Release process

## Release preparation

The following items should be checked before starting the release process:

-   Check that the generated Javadoc contains the appropriate set of packages, i.e. only the public API. This excludes classes from `axiom-impl` and `axiom-dom` as well as classes related to unit tests.

-   Check that all dependencies and plugins are available from standard repositories. To do this, clean the local repository and execute `mvn clean install` followed by `mvn site`.

-   Check that the set of license files in the `legal` directory is complete and accurate (by checking that in the binary distribution, there is a license file for every third party JAR in the `lib` folder).

-   Check that the Maven site conforms to the latest version of the [Apache Project Branding Guidelines](http://apache.org/foundation/marks/pmcs).

-   Check that the `apache-release` profile can be executed properly. To do this, issue the following command:

    ```sh
    mvn clean install -Papache-release -DskipTests=true
    ```

    You may also execute a dry run of the release process:

    ```sh
    mvn release:prepare -DdryRun=true
    ```

    After this, you need to clean up using the following command:

    ```sh
    mvn release:clean
    ```

-   Check that the Maven site can be generated and deployed successfully, and that it has the expected content.

-   Complete the release note (`src/site/markdown/release-notes/<version>.md`). It should include a description of the major changes in the release as well as a list of resolved JIRA issues.

## Prerequisites

The following things are required to perform the actual release:

-   A PGP key that conforms to the [requirement for Apache release signing](http://www.apache.org/dev/release-signing.html). To make the release process easier, the passphrase for the code signing key should be configured in `${user.home}/.m2/settings.xml`:

    ```xml
    <settings>
      ...
      <profiles>
        <profile>
          <id>apache-release</id>
          <properties>
            <gpg.passphrase><!-- KEY PASSPHRASE --></gpg.passphrase>
          </properties>
        </profile>
      </profiles>
      ...
    </settings>
    ```

-   The release process uses a Nexus staging repository. Every committer should have access to the corresponding staging profile in Nexus. To validate this, login to `repository.apache.org` and check that you can see the `org.apache.ws` staging profile. The credentials used to deploy to Nexus should be added to `settings.xml`:

    ```xml
    <servers>
      ...
      <server>
        <id>apache.releases.https</id>
        <username><!-- ASF username --></username>
        <password><!-- ASF LDAP password --></password>
      </server>
      ...
    </servers>
    ```

## Release

In order to prepare the release artifacts for vote, execute the following steps:

1.  If necessary, update the copyright date in the top level `NOTICE` file.

2.  Start the release process with the following command - use `mvn release:rollback` to undo and be aware that in the main `pom.xml` there is an apache parent that defines some plugin versions. See <https://maven.apache.org/pom/asf>

    ```sh
    mvn release:prepare
    ```

    When asked for the "SCM release tag or label", keep the default value (`x.y.z`).

    The above command will create a tag in Subversion and increment the version number of the trunk to the next development version. It will also create a `release.properties` file that will be used in the next step.

3.  Perform the release using the following command:

    ```sh
    mvn release:perform
    ```

    This will upload the release artifacts to the Nexus staging repository.

4.  Log in to the Nexus repository (<https://repository.apache.org/>) and close the staging repository. The name of the staging profile is `org.apache.ws`. See <https://maven.apache.org/developers/release/maven-project-release-procedure.html> for a more thorough description of this step.

5.  Execute the `target/checkout/etc/dist.py` script to upload the source and binary distributions to the development area of the <https://dist.apache.org/repos/dist/> repository.

    If not yet done, export your public key and append it to <https://dist.apache.org/repos/dist/release/ws/axiom/KEYS>. The command to export a public key is as follows:

    ```sh
    gpg --armor --export <key_id>
    ```

    If you have multiple keys, you can define a `~/.gnupg/gpg.conf` file for a default. Note that while `gpg --list-keys` will show your public keys, using maven-release-plugin with the command `release:perform` below requires `gpg --list-secret-keys` to have a valid entry that matches your public key, in order to create `asc` files that are used to verify the release artifcats. `release:prepare` creates the sha512 checksum files.

    The created artifacts i.e. zip files can be checked with, for example, `sha512sum 'axiom-2.0.0-bin.zip'` which should match the generated sha512 files. In that example, use `gpg --verify axiom-2.0.0-bin.zip.asc axiom-2.0.0-bin.zip.asc` to verify the artifacts were signed correctly.

6.  Delete <https://svn.apache.org/repos/asf/webservices/website/axiom-staging/> if it exists. Create a new staging area for the site:

    ```sh
    svn copy \
    https://svn.apache.org/repos/asf/webservices/website/axiom \
    https://svn.apache.org/repos/asf/webservices/website/axiom-staging
    ```

    This step can be skipped if the staging area has already been created earlier (e.g. to test a snapshot version of the site).

7.  Change to the `target/checkout` directory and prepare the site using the following commands:

    ```sh
    mvn site-deploy
    mvn scm-publish:publish-scm -Dscmpublish.skipCheckin=true
    ```

    The staging area will be checked out to `target/scmpublish-checkout` (relative to `target/checkout`). Do a sanity check on the changes and then commit them.

8.  Start the release vote by sending a mail to `dev@ws.apache.org`. The mail should mention the following things:

    - The list of issues solved in the release (by linking to the relevant JIRA view).
    - The location of the Nexus staging repository.
    - The link to the source and binary distributions: `https://dist.apache.org/repos/dist/dev/ws/axiom/<version>`.
    - A link to the preview of the Maven site: <http://ws.apache.org/axiom-staging/>.

If the vote passes, execute the following steps:

1.  Promote the artifacts in the staging repository. See <https://central.sonatype.org/publish/release/#close-and-drop-or-release-your-staging-repository> for detailed instructions for this step.

2.  Publish the distributions:

    ```sh
    svn mv https://dist.apache.org/repos/dist/dev/ws/axiom/<version> \
          https://dist.apache.org/repos/dist/release/ws/axiom/
    ```

    `<version>` is the release version, e.g. `1.2.9`.

3.  Publish the site:

    ```sh
    svn co --depth=immediates https://svn.apache.org/repos/asf/webservices/website ws-site
    cd ws-site
    svn rm axiom
    svn mv axiom-staging axiom
    svn commit
    ```

It may take several hours before all the updates have been synchronized to the relevant ASF systems. Before proceeding, check that:

- the Maven artifacts for the release are available from the Maven central repository;
- the Maven site has been synchronized to <http://ws.apache.org/axiom/>;
- the binary and source distributions can be downloaded from <http://ws.apache.org/axiom/download.html>.

Once everything is in place, send announcements to `users@ws.apache.org` and `announce@apache.org`. Since the two lists have different conventions, audiences and moderation policies, to send the announcement separately to the two lists.

Sample announcement:

> Apache Axiom Team is pleased to announce the release of Axiom x.y.z. The release is available
> for download at:
>
> http://ws.apache.org/axiom/download.cgi
>
> Apache Axiom is a StAX-based, XML Infoset compliant object model which supports on-demand building
> of the object tree. It supports a novel "pull-through" model which allows one to turn off the tree
> building and directly access the underlying pull event stream. It also has built in support for
> XML Optimized Packaging (XOP) and MTOM, the combination of which allows XML to carry binary
> data efficiently and in a transparent manner. The combination of these is an easy to use API
> with a very high performant architecture!
>
> Developed as part of Apache Axis2, Apache Axiom is the core of Apache Axis2. However, it is a
> pure standalone XML Infoset model with novel features and can be used independently of Apache Axis2.
>
> Highlights in this release:
>
> - ...
> - ...
>
> Resolved JIRA issues:
>
> - [WSCOMMONS-513] Behavior of insertSiblingAfter and insertSiblingBefore is not well defined for orphan nodes
> - [WSCOMMONS-488] The sequence of events produced by OMStAXWrapper with inlineMTOM=false is inconsistent

For `users@ws.apache.org`, the subject ("Axiom x.y.z released") should be prefixed with "[ANN][Axiom]", while for `announce@apache.org` "[ANN]" is enough. Note that mail to `announce@apache.org` must be sent from an `apache.org` address.

## Post-release actions

- Update the DOAP file (see `etc/axiom.rdf`) and add a new entry for the release.
- Update the status of the release version in the AXIOM project in JIRA.
- Remove archived releases from <https://dist.apache.org/repos/dist/release/ws/axiom/>.

## References

The following documents are useful when preparing and executing the release:

- [ASF Source Header and Copyright Notice Policy](http://www.apache.org/legal/src-headers.html)
- [Apache Project Branding Guidelines](http://apache.org/foundation/marks/pmcs)
- [DOAP Files](http://projects.apache.org/doap.html)
- [Publishing Releases](http://www.apache.org/dev/release-publishing.html)
