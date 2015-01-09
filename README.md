urlshortener
============

(Under Development, incomplete)

Simple web service which can shorten and expand a URL, in a tinyurl / bitly fashion.

This project is primarily a thought exercise and a way of trying out a few different ideas.  It was built using TDD/BDD so should have good test coverage.  Particular attention has been paid to efficient use of RAM, and fast shortening/expansion of tokens.

The shortener is a JSON web service, and does not have a pretty GUI for ordinary web users.  In a future iteration I may add a GUI.

There are two 'databases' available:

 1. An optimised RAM based map, which can persist to disk.
 2. A MapDB backed disk store which can support significantly larger data sets.

TODO:
 * Support Distributed, load balanced architecture. Maybe with http://www.jgroups.org/
     * manage/distribute ranges of tokens
     * Leadership election
     * Replication
     * Add server to the group
 * GUI?
 * anti-spam/anti-phishing?
 * track statistics?


References
==========
 * http://devslovebacon.com/conferences/bacon-2014/talks/lessons-learned-building-distributed-systems-at-bitly
 * http://www.mapdb.org/ and https://github.com/jankotek/MapDB
