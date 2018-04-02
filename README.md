# Classifier-Trainer
An exercise generator, which masks Chinese, Japanese and Vietnamese classifiers (using GWT, fudannlp, kuromoji, UETsegmenter, RDRPOSTagger).

![Graphic](https://github.com/leduvu/Classifier-Trainer/blob/master/pictures/demo1.png)
![Graphic](https://github.com/leduvu/Classifier-Trainer/blob/master/pictures/demo2.png)


<br />
<br />
## General Information
The program code was written in Java 8 using the Google Web Toolkit (in short: gwt) to create a web based application. For the sake of simplicity an IDE called eclipse was used. Therefore, it is always possible to import the existing project into eclipse and run it on a local computer. To start the web based application simply right click on the project and choose ”Run as” → ”GWT Super Dev Mode”. This mode should ensure, that no browser plug in is needed to see the program on any browser. A link will appear in the console of eclipse, which can be clicked or copy- pasted into the browsers address bar. Then, the program will compile first before it can be seen and used. Should no classifiers be found, please also try to set the encoding to UTF8 under preferences.

Another way to use the program is to deploy the application to tomcat on a web server. In this case it could be used by everyone with access to the Internet, if the web address is known.

There are different programs included, to perform tokenization and pos-tagging. These were written by other programmers and are licensed, except the UETseg- menter1 a tokenizer for Vietnamese. For the tokenization of Japanese a tokenizer from atilika called kuromoji2 with the version 0.9.0 was used. It includes the ipadic dictonary for the tokenization and the whole program is under the Apache License v2.0. To tokenize Chinese text fudannlp3 was used, which is under the GNU Lesser GPL license. The pos-tagging was performed by the RDRPOSTagger4 for all three languages, to keep the pos tags consistent. Therefore, the universal corpus was used, even though there was also an option for an optimzed vietnamese pos-tagger. The pos-tagger is under the GNU General Public License.

<br />
<br />
## Usage
As instructed on the page, the user can paste texts of the language they want to practice for into the gray textarea and check the language and the levels before starting the exercise as shown in Figure 1. It is allowed to check more than one level as the user likes. When the text is masked, below is shown how many classifiers are found. The classifiers or rather any text can be typed into the text box, and after the solutions are submitted by clicking the finished button, feedback is given in green as correct and red as incorrect, over which the user can hover the cursor for additional information about the classifier. The back button will lead the user back to the starting point, where the pasted text is still in the textarea and the previous selected levels and languages remain unchanged.

<br />

(Programmed by Le Duyen Sandra Vu. Contributor Chih-Chun Chang.)
