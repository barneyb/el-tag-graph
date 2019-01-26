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

## The Problem

The problem is how to suggest the most likely tags based on historical usage.
A naive solution is to simply count how many times each tag was used, and then
suggest the top N most-used tags. This falls down on a couple counts, however:

1.  it has no sensitivity to "recentness"
1.  it has no sensitivity to groups of tags used together

Recent events should have more weight than events "long" ago. Which is to say
that today is probably more similar to one day ago than to one year ago. This is
trivially addressed by weighting tag usage via age when calculating total use.

For example, if I used swim every day, but I recently moved and my new gym
doesn't have a pool, I would want a "swimming" tag to drop off the list of
suggestions fairly quickly and be replaced by "basketball" (or whatever my new
chosen activity is). Specifically, I shouldn't have to wait until I've played
basketball as many times as I went swimming before they switch places; swimming
should fall off quickly since it's uses are longer and longer ago.

The latter is more subtle. If two tags are often used together, showing them
both is probably unneeded, because one of them must be picked first. So only
showing one allows for a broader gamut of initial options, but doesn't preclude
getting both tags with two clicks, because the second should become available as
soon as the first is chosen.

For example, if I usually warm up on the treadmill before playing basketball,
the "treadmill" tag probably doesn't need to be suggested until "basketball" has
been chosen. This assumes that I use the treadmill _without_ playing basketball
fairly infrequently. If I _do_ use the treadmill in isolation, it should still
be available as a first pick, as it stands alone.
