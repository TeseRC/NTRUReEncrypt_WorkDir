
package nics.crypto.ntrureencrypt;

import java.util.Arrays;
import net.sf.ntru.encrypt.EncryptionKeyPair;
import net.sf.ntru.encrypt.EncryptionParameters;
import net.sf.ntru.polynomial.IntegerPolynomial;


public class Tese1 {
	
	// 
    static EncryptionParameters[] eps = {
        EncryptionParameters.EES1087EP2, //0
        EncryptionParameters.EES1087EP2_FAST, //1
        EncryptionParameters.EES1171EP1, // 2
        EncryptionParameters.EES1171EP1_FAST, // 3
        EncryptionParameters.EES1499EP1, // 4
        EncryptionParameters.EES1499EP1_FAST, // 5
        EncryptionParameters.APR2011_439, // 6
        EncryptionParameters.APR2011_439_FAST, // 7
        EncryptionParameters.APR2011_743, // 8
        EncryptionParameters.APR2011_743_FAST // 9
    };

    public static void main(String[] args) throws Exception {
    	test2();
    	test1();
        
    }

    public static void test1() throws Exception {
    	
    	int NITER = 10;
    	int i, len;
    	long startTime = 0;
    	long endTime = 0;
    	long duration = 0;
    	long durEnc = 0;
    	long durDec = 0;
    	
    	IntegerPolynomial m, m2, cA;
    	
    	EncryptionParameters ep = eps[3];   // EES1171EP1_FAST
        NTRUReEncrypt ntruReEnc = new NTRUReEncrypt(ep);
    	
    	System.out.println("Starting Test1...");
    	System.out.println("max len message params: " + ep.getMaxMessageLength());
        System.out.println("output len: " + ep.getOutputLength());
        System.out.println("N: " + ep.N);
        System.out.println("q: " + ep.q);
        System.out.println("hashCode: " + ep.hashCode());
        System.out.println("hash algo: " + ep.hashAlg);
    	
        String msg1 = "The quick brown fox ## ";
        String msg =
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "mais uma frase com bytes para processar. Sera que vai passar, a ver vamos como diz o " +
    			"ja vai em 201B, sera que passa os 220?" +
                "##";
        
        byte[] msgBytes = msg.getBytes();
        System.out.print("msg: ");
        for(i=0; i<msgBytes.length; i++) {
        	System.out.print((char)msgBytes[i]);
        }
        System.out.println(" ");
        System.out.println("msgBytes: " + Arrays.toString(msgBytes));
        System.out.println("msg len: " + msgBytes.length);
        
        //m = ntruReEnc.message(msg.getBytes()); 
        m = ntruReEnc.msg2trin(msg.getBytes());
        System.out.println("msg coefs = " + Arrays.toString(m.coeffs));
        System.out.println("msg coefs len= " + Arrays.toString(m.coeffs).length());
        
        for(i = 0; i < NITER; i++) {
        	
	        EncryptionKeyPair kpA = ntruReEnc.generateKeyPair();
	        //m = ntruReEnc.message(new byte[]{122,2,56});
	
	        startTime = System.nanoTime();
	        cA = ntruReEnc.encrypt(kpA.getPublic(), m);
	        endTime = System.nanoTime();
	    	duration = (endTime - startTime);
	    	durEnc += duration;
	    	
	    	System.out.println("msgEnc coefs = " + Arrays.toString(cA.coeffs));
	        System.out.println("msgEnc coefs len= " + Arrays.toString(cA.coeffs).length());
	        
	        startTime = System.nanoTime();
	        m2 = ntruReEnc.decrypt(kpA.getPrivate(), cA);
	        endTime = System.nanoTime();
	    	duration = (endTime - startTime);
	    	durDec += duration;
	    	
	    	if (Arrays.equals(m.coeffs, m2.coeffs)) {
	            //System.out.println("Test 1 OK!");
	        } else {
	            System.out.println("Test 1 Failed!");
	        }
	    	
	    	
	    	System.out.println("msgDec coefs = " + Arrays.toString(m2.coeffs));
	        System.out.println("msgDec coefs len= " + Arrays.toString(m2.coeffs).length());
	    	byte[] msgFinalBytes = ntruReEnc.trin2msg(m2);
	    	System.out.print("msgFinal: ");
	        for(i=0; i<msgFinalBytes.length; i++) {
	        	System.out.print((char)msgFinalBytes[i]);
	        }
	        System.out.println(" ");
	        System.out.println("msgFinalBytes: " + Arrays.toString(msgFinalBytes));
	        System.out.println("msgFinal len: " + msgFinalBytes.length);
	        
        }
        
        System.out.println("Test 1 OK!");
        System.out.println("Encrypt duration: " + durEnc/(NITER*1000) + " us");
    	System.out.println("decrypt duration: " + durDec/(NITER*1000) + " us");
    }
    
    public static void test2() throws Exception {
    	
    	int NITER = 10;
    	long startTime = 0;
    	long endTime = 0;
    	long duration = 0;
    	long durEnc = 0;
    	long durReEnc = 0;
    	long durDec = 0;
    	IntegerPolynomial m, m2, cA, cB, aux;
    	m = m2 = cA = cB = aux = new IntegerPolynomial(new int[] {0}); // init coeff vectors of poly funcs
    	
    	// setup NTRUReEnc instance with selected parameters 
        EncryptionParameters ep = eps[3];   // EES1171EP1_FAST
        NTRUReEncrypt ntruReEnc = new NTRUReEncrypt(ep);
        
        System.out.println("Starting Test2...");
        
        // the string test message to be considered
        String msg = "The quick brown fox " +
        			"incididunt ut labore ethehet dolore magna aliqua. Ut enim ad im veniam, quis nostrud " +
        			"mais uma frase com bytes para processar. Sera que vai passar, a ver vamos como diz o " +
        			"ja vai em 201B, sera que passa os 220?" +
        			"##";
        String msg1 =
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna alhethteiqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqhehteua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dotgreglore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut hethenim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quhehis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "incididunt ut labore ethehet dolore magna aliqua. Ut enim ad minim veniam, quis nostrud" +
                "##";
        
        System.out.println("msgBytes: " + Arrays.toString(msg.getBytes()));
        System.out.println("msg len: " + msg.getBytes().length);
        
        // create a random message in polynomial vector format
        // this is a random message in polynomial format and coeffs are ternary (0,1,2) -> (-1,0,1)
        //m = ntruReEnc.message(new byte[]{122,2,56});
        
        // convert message bytes to trinary coeffs
        m = ntruReEnc.msg2trin(msg.getBytes());
        System.out.println("msg coefs = " + Arrays.toString(m.coeffs));
        System.out.println("msg coefs len= " + Arrays.toString(m.coeffs).length());
        
        for(int i = 0; i < NITER; i++) {
        	
        	// generate key pairs (secret, public) for A and B nodes
	        EncryptionKeyPair kpA = ntruReEnc.generateKeyPair();
	        EncryptionKeyPair kpB = ntruReEnc.generateKeyPair();
	        
	        startTime = System.nanoTime();
	        // encrypt message for node A, this is, generate ciphertext cA
	        cA = ntruReEnc.encrypt(kpA.getPublic(), m);
	        endTime = System.nanoTime();
	    	duration = (endTime - startTime); 
	    	durEnc += duration;
	        
	    	System.out.println("cA coefs = " + Arrays.toString(cA.coeffs));
	        System.out.println("cA coefs len= " + Arrays.toString(cA.coeffs).length());
	    	
	        // generate RK to allow the transformation: cA into cB 
	        ReEncryptionKey rk = ntruReEnc.generateReEncryptionKey(kpA, kpB);
	        
	        // re-encrypt cA into cB, using RK
	        startTime = System.nanoTime();
	        cB = ntruReEnc.reEncrypt(rk, cA);
	        endTime = System.nanoTime();
	    	duration = (endTime - startTime); 
	    	durReEnc += duration;
	    	
	    	System.out.println("cB coefs = " + Arrays.toString(cB.coeffs));
	        System.out.println("cB coefs len= " + Arrays.toString(cB.coeffs).length());
	    	
	    	// node B decrypts cB with its own secret key
	    	startTime = System.nanoTime();
	    	m2 = ntruReEnc.decrypt(kpB.getPrivate(), cB);
	        endTime = System.nanoTime();
	    	duration = (endTime - startTime);
	    	durDec += duration;
	    	
	    	byte[] msgFinalBytes = ntruReEnc.trin2msg(m2);
	    	String msgStringFinal ="";
	    	
	    	for(i=0; i<msgFinalBytes.length; i++) {
	    		char c = (char)msgFinalBytes[i];
	    		msgStringFinal += c;
	        }
	    	
	    	
	    	System.out.println("msgFinal coefs = " + Arrays.toString(m2.coeffs));
	        System.out.println("msgFinal coefs len= " + Arrays.toString(m2.coeffs).length());
	        System.out.println("msgFinalBytes: " + Arrays.toString(msgFinalBytes));
	        System.out.println("msgFinalBytes len: " + msgFinalBytes.length);
	        System.out.println("msgFinal: " + msgStringFinal);
	        System.out.println("msgFinal: " + msgStringFinal.length());
	        
	        
        }

    	
        if (Arrays.equals(m.coeffs, m2.coeffs)) {
            System.out.println("Test 2 OK!");
        } else {
            System.out.println("Test 2 Failed!");
        }
        
        System.out.println("Encrypt duration:  " + durEnc/(NITER*1000) + " us");
    	System.out.println("decrypt duration:  " + durDec/(NITER*1000) + " us");
    	System.out.println("reEcrypt duration: " + durReEnc/(NITER*1000) + " us");
        
        
    }
    
   
}
