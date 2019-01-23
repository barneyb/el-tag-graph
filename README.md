# Tag Graph (for EventLog)

I aim to provide useful graph processing to support human UIs which front an
arbitrary-size and -connectedness graph of tags. For example, the
[el-tag-editor](https://github.com/barneyb/el-tag-editor) component might use
it to drive the "next tags" feature.

At the moment, however, it's just a playground.

## Building

Standard Java/Maven stuff:

    $ mvn --version
    Java HotSpot(TM) 64-Bit Server VM
    Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-10T08:41:47-08:00)
    Java version: 1.8.0_20, vendor: Oracle Corporation
    $ mvn package
    ... the build output ...
    $ java -jar target/tag-graph-0.0.1-SNAPSHOT.jar 
    hello, world!

It's very exciting.
