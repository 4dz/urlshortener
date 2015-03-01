urlshortener
============

Simple, scalable, optionally replicated web service which can shorten and expand a URL, in a tinyurl / bitly fashion.

This project is primarily a thought exercise and a way of trying out a few different ideas.  It was built using TDD/BDD so should have good test coverage.  Particular attention has been paid to efficient use of RAM, and fast shortening/expansion of tokens.

The URL shortener is a JSON web service, and does not have a pretty GUI for ordinary web users.  In a future iteration I may add a GUI.

There are three 'databases' available:

 1. An optimised RAM based map, which can persist to disk.  Limited by available RAM.
 2. A MapDB backed disk store which can support significantly larger data sets. Limited by disk space.
 3. A MapDB+JGroups distributed store which can replicate across multiple servers. Limited by disk space.

Configuration
=============
There is a config.properties file which configures the system.

    BASE_URL=http://b.io/
    DISK_BACKUP_FILEPATH=/var/lib/urlshortener/backup.mapdb
    DATABASE_FACTORY_CLASSPATH=com.perry.urlshortener.persistence.BigOrderedReplicatedMapDBSetFactory
    REPLICATION_CLUSTER_NAME=urlshortenermap
    REPLICATION_TIMEOUT_MS=5000

If you have multiple instances running on the same file system, you will need to give each instance a different backup file path.  This can be done by starting the JVM with -DDISK_BACKUP_FILEPATH=/var/lib/urlshortener/backup_2.mapdb (for example).

Usage
=====
Assuming the system is running on http://b.io/,

To shorten a URL:
 http://b.io/?shorten=http://some.url/etc/

This will return a shortened URL e.g. http://b.io/jhakdj
Hitting that URL will take you to the originally shortened URL.

To view the expanded URL without redirecting to it, simply add a +
 http://b.io/xhgjgh+

A JSONP service also exists to allow manipulatin of URLs with JavaScript.
Simply add a ?callback= parameter to the request, e.g.

 http://b.io/xhghg?callback=myfunc
 
This will return e.g.

    myfunc({"url": "http://fred.1"});

Equally shortening a URL can return JSONP responses;

 http://b.io/?shorten=http://bob&callback=myfunc

    myfunc({"url": "http://b.io/ashdk"});

References
==========
 * http://devslovebacon.com/conferences/bacon-2014/talks/lessons-learned-building-distributed-systems-at-bitly
 * http://www.mapdb.org/ and https://github.com/jankotek/MapDB
 * http://www.jgroups.org/ and https://github.com/belaban/JGroups
