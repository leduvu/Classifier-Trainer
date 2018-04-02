# Classifier-Trainer
An exercise generator, which masks Chinese, Japanese and Vietnamese classifiers (using GWT, fudannlp, kuromoji, UETsegmenter, RDRPOSTagger).

<br />
<br />
The program code was written in Java 8 using the Google Web Toolkit (in short: gwt) to create a web based application. For the sake of simplicity an IDE called eclipse was used. Therefore, it is always possible to import the existing project into eclipse and run it on a local computer. To start the web based application simply right click on the project and choose ”Run as” → ”GWT Super Dev Mode”. This mode should ensure, that no browser plug in is needed to see the program on any browser. A link will appear in the console of eclipse, which can be clicked or copy- pasted into the browsers address bar. Then, the program will compile first before it can be seen and used. Should no classifiers be found, please also try to set the encoding to UTF8 under preferences.

Another way to use the program is to deploy the application to tomcat on a web server. In this case it could be used by everyone with access to the Internet, if the web address is known.

There are different programs included, to perform tokenization and pos-tagging. These were written by other programmers and are licensed, except the UETseg- menter1 a tokenizer for Vietnamese. For the tokenization of Japanese a tokenizer from atilika called kuromoji2 with the version 0.9.0 was used. It includes the ipadic dictonary for the tokenization and the whole program is under the Apache License v2.0. To tokenize Chinese text fudannlp3 was used, which is under the GNU Lesser GPL license. The pos-tagging was performed by the RDRPOSTagger4 for all three languages, to keep the pos tags consistent. Therefore, the universal corpus was used, even though there was also an option for an optimzed vietnamese pos-tagger. The pos-tagger is under the GNU General Public License.

<br />
<br />
<br />

(Programmed by Le Duyen Sandra Vu. Contributor Chih-Chun Chang.)
