# clj-shoutcast

Shoutcast servers are used to stream many internet radio stations.  This library extends [BasicPlayer](http://www.javazoom.net/jlgui/api.html) and incorporates a fix adapted from [https://github.com/whamtet/shoutcast-applet](https://github.com/whamtet/shoutcast-applet) to provide a shoutcast client.  You can use it to make your own internet radio player.

Another popular format similar to Shoutcast is called Icyformat, it's currently unimplemented.  If you wish to do this, look at the source code in shoutcast-applet.

Be careful not to touch the sample http header in header.txt, Shoutcast servers will fail if you omit just one trailing \r\n.

## Usage

Read the code.

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
