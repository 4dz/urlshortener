urlshortener
============

(Under Development, incomplete)

Simple web service which can shorten and expand a URL, in a tinyurl / bitly fashion.

This project is primarily a thought exercise and a way of trying out a few different ideas.  It was built using TDD/BDD so should have good test coverage.  Particular attention has been paid to efficient use of RAM, and fast shortening/expansion of tokens.

The shortener is a JSON web service, and does not have a pretty GUI for ordinary web users.  In a future iteration I may add a GUI.

TODO:

 * play with Berkely DB for scalability
 * anti-spam/anti-phishing?
 * track statistics?
 * GUI

References
==========
 * http://devslovebacon.com/conferences/bacon-2014/talks/lessons-learned-building-distributed-systems-at-bitly
