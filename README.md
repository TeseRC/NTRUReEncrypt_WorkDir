# NTRUReEncrypt_WorkDir
My working directory of Eclipse NTRUReEncypt project

Using the JAVA Bouncy Castle Crypto library in:
https://www.bouncycastle.org/about.html.
The NTRU crypto system is built in: 
https://github.com/tbuktu/ntru.
NTRU public-key cryptosystem consists of the encryption scheme NTRUEncrypt and the signature scheme NTRUSign.

On top of NTRU, the NTRUReEncrypt PRE system is built and available in:
https://github.com/cygnusv/ntrureencrypt.

Over NTRUReEncrypt we build our scripts and different implementations to measure performance and limitations. 

"ReEncLimits.java" contains the code to test the re-encryption capabilities, by specifying the NTRU parameters, the number of re-encryptions to perform and the number of iterations.
"Timings.java" contains the code to test the timing performance for the different NTRUReEncrypt functions.
"Tese1.java" contains miscellaneous code to test and observe the NTRU variables, such as the different vectors created, coefficients, messages, etc. 
