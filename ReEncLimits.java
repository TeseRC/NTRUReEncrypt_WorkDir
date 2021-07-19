
package nics.crypto.ntrureencrypt;

import java.util.Arrays;
import net.sf.ntru.encrypt.EncryptionKeyPair;
import net.sf.ntru.encrypt.EncryptionParameters;
import net.sf.ntru.polynomial.IntegerPolynomial;
import java.io.FileWriter;   // Import the FileWriter class

public class ReEncLimits {
	
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
    	
    	int nFails, successPercent;
    	int niter = 300;  // number of iterations
    	int nReEnc = 45;  // number of re-encryptions to consider
    	int encryptionPrams = 5;  // encryption parameters
    	boolean debug = false;  // more verbose logs to verify message enc, reEnc and decrypt phases
    	
    	System.out.println("Starting ReEncryptions Limit test");
    	
    	FileWriter myWriter = new FileWriter("ReEncRatio.txt");
    	
    	for(int i=5; i<=nReEnc; i++) {
    		nFails=0;
    		for(int j=0; j<niter; j++) {
        		
        		nFails += test(i, debug, j, encryptionPrams);	
        	}
    		
    		successPercent = (niter - nFails) * 100 / niter;
    		
    		//myWriter.write("#ReEnc: " + i + " | Success rate: " + successPercent + "%");
    		myWriter.write(successPercent);
    		myWriter.write("\n");
    	}
    	
    	myWriter.close();
    	System.out.println("Successfully wrote to the file.");
        
    }

    public static int test(int nReEnc, boolean debug, int j, int encp) throws Exception {
    	
    	int count = 0;
    	int coefs = 0;
    	long startTime = 0;
    	long endTime = 0;
    	long duration = 0;
    	long durEnc = 0;
    	long durReEnc = 0;
    	long durDec = 0;
    	IntegerPolynomial m, m2, cA, cB, cX, cY, aux;
    	m = m2 = cA = cB = cX = cY = aux = new IntegerPolynomial(new int[] {0}); // init coeff vectors of poly funcs
    	
    	
    	FileWriter rkCoefsWriter;
    	FileWriter cipherCoefsWriter;
    	if(j==0) {
    		rkCoefsWriter = new FileWriter("rkCoefs.txt");
    		cipherCoefsWriter = new FileWriter("cipherCoefs.txt");
    	} 
    	else {
    		rkCoefsWriter = new FileWriter("rkCoefsAux.txt");
    		cipherCoefsWriter = new FileWriter("cipherCoefsAux.txt");
    	} 
    	
    	// setup NTRUReEnc instance with selected parameters 
        EncryptionParameters ep = eps[encp];   // EES1171EP1_FAST
        NTRUReEncrypt ntruReEnc = new NTRUReEncrypt(ep);
        
        if(debug) System.out.println("Starting Test...");
        
        // the string test message to be considered
        String msg = "The quick brown fox " +
        			"incididunt ut labore ethehet dolore magna aliqua. Ut enim ad im veniam, quis nostrud " +
        			"mais uma frase com bytes para processar. Sera que vai passar, a ver vamos como diz o " +
        			"ja vai em 201B, sera que passa os 220?" +
        			"##";
        
        
        if(debug) System.out.println("msgBytes: " + Arrays.toString(msg.getBytes()));
        if(debug) System.out.println("msg len: " + msg.getBytes().length);
        
        // convert message bytes to trinary coeffs
        m = ntruReEnc.msg2trin(msg.getBytes());
        
        if(debug) System.out.println("msg coefs = " + Arrays.toString(m.coeffs));
        if(debug) System.out.println("msg coefs len= " + Arrays.toString(m.coeffs).length());
    
    	// generate key pairs (secret, public) for A and B nodes
        EncryptionKeyPair kpA = ntruReEnc.generateKeyPair();
        EncryptionKeyPair kpB = ntruReEnc.generateKeyPair();
        EncryptionKeyPair kpX = ntruReEnc.generateKeyPair();
        EncryptionKeyPair kpY = ntruReEnc.generateKeyPair();
        
        startTime = System.nanoTime();
        // encrypt message for node A, this is, generate ciphertext cA
        cA = ntruReEnc.encrypt(kpA.getPublic(), m);
        //System.out.println(cA.toString());
        //System.out.println(msg.length());
        
        endTime = System.nanoTime();
    	duration = (endTime - startTime); 
    	durEnc += duration;
        
    	if(debug) System.out.println("cA coefs = " + Arrays.toString(cA.coeffs));
    	if(debug) System.out.println("cA coefs len= " + Arrays.toString(cA.coeffs).length());
    
        	
        // generate RK to allow the transformation: cA into cX 
    	ReEncryptionKey rkX = ntruReEnc.generateReEncryptionKey(kpA, kpX);
    	coefs = rkX.rk.sumCoeffs();
    	
        // re-encrypt cB into cX, using RK 
    	cX = ntruReEnc.reEncrypt(rkX, cA);
    	count++;
    	if(j == 0) {
    		rkCoefsWriter.write(coefs + "\n");
    		if(debug) System.out.println("RK " + count + ": " + coefs);
    		cipherCoefsWriter.write(cX.sumCoeffs() + "\n");
    		if(debug) System.out.println("RC " + count + ": " + cX.sumCoeffs());
    	} 
    	
    	
    	while((count + 2) <= nReEnc) {
    		EncryptionKeyPair kpAux = ntruReEnc.generateKeyPair();
        	ReEncryptionKey rkAux = ntruReEnc.generateReEncryptionKey(kpX, kpAux);
        	aux = ntruReEnc.reEncrypt(rkAux, cX);
        	count++;
        	coefs = rkAux.rk.sumCoeffs();
        	if(j == 0) {
        		rkCoefsWriter.write(coefs + "\n");
        		if(debug) System.out.println("RK " + count + ": " + coefs);
        		cipherCoefsWriter.write(cX.sumCoeffs() + "\n");
        		if(debug) System.out.println("RC " + count + ": " + cX.sumCoeffs());
        	} 
        	
        	if((count + 2) >= nReEnc){
        		kpX = kpAux;
        		rkX = rkAux;
        		cX = aux;
        		break;
        	}
        	
        	kpX = ntruReEnc.generateKeyPair();
        	rkX = ntruReEnc.generateReEncryptionKey(kpAux, kpX);
        	cX = ntruReEnc.reEncrypt(rkX, aux);
        	count++;
        	coefs = rkX.rk.sumCoeffs();
        	if(j == 0) {
        		rkCoefsWriter.write(coefs + "\n");
        		if(debug) System.out.println("RK " + count + ": " + coefs);
        		cipherCoefsWriter.write(cX.sumCoeffs() + "\n");
        		if(debug) System.out.println("RC " + count + ": " + cX.sumCoeffs());
        	} 
        	
        	if((count + 2) >= nReEnc){
        		break;
        	}
    	}
    	
        // generate RK to allow the transformation: cX into cY 
    	ReEncryptionKey rkY = ntruReEnc.generateReEncryptionKey(kpX, kpY);
        // re-encrypt cX into cY, using RK 
    	cY = ntruReEnc.reEncrypt(rkY, cX);
    	count++;
    	coefs = rkY.rk.sumCoeffs();
    	if(j == 0) {
    		rkCoefsWriter.write(coefs + "\n");
    		if(debug) System.out.println("RK " + count + ": " + coefs);
    		cipherCoefsWriter.write(cX.sumCoeffs() + "\n");
    		if(debug) System.out.println("RC " + count + ": " + cX.sumCoeffs());
    	} 
    	
        // generate RK to allow the transformation: cA into cB 
        ReEncryptionKey rkB = ntruReEnc.generateReEncryptionKey(kpY, kpB);
        
        // re-encrypt cX into cB, using RK
        startTime = System.nanoTime();
        cB = ntruReEnc.reEncrypt(rkB, cY);
        endTime = System.nanoTime();
    	duration = (endTime - startTime); 
    	durReEnc += duration;
    	count++;
    	coefs = rkB.rk.sumCoeffs();
    	if(j == 0) {
    		rkCoefsWriter.write(coefs + "\n");
    		if(debug) System.out.println("RK " + count + ": " + coefs);
    		cipherCoefsWriter.write(cX.sumCoeffs() + "\n");
    		if(debug) System.out.println("RC " + count + ": " + cX.sumCoeffs());
    	} 
    	
    	
    	if(debug) System.out.println("number of re-encryptions = " + count);
    	if(debug) System.out.println("cB coefs = " + Arrays.toString(cB.coeffs));
    	if(debug) System.out.println("cB coefs len= " + Arrays.toString(cB.coeffs).length());
    	
    	// node B decrypts cB with its own secret key
    	startTime = System.nanoTime();
    	m2 = ntruReEnc.decrypt(kpB.getPrivate(), cB);
        endTime = System.nanoTime();
    	duration = (endTime - startTime);
    	durDec += duration;
    	
    	if(debug) System.out.println("msgFinal coefs = " + Arrays.toString(m2.coeffs));
    	if(debug) System.out.println("msgFinal coefs len= " + Arrays.toString(m2.coeffs).length());
    	
    	//byte[] msgFinalBytes = ntruReEnc.trin2msg(m2);
    	String msgStringFinal ="start coefs = end coefs";
    	
    	//for(i=0; i<msgFinalBytes.length; i++) {
    		//char c = (char)msgFinalBytes[i];
    		//msgStringFinal += c;
        //}
    	
    	if(debug) System.out.println("msgFinal coefs = " + Arrays.toString(m2.coeffs));
    	if(debug) System.out.println("msgFinal coefs len= " + Arrays.toString(m2.coeffs).length());
       // System.out.println("msgFinalBytes: " + Arrays.toString(msgFinalBytes));
        //System.out.println("msgFinalBytes len: " + msgFinalBytes.length);
    	if(debug) System.out.println("msgFinal: " + msgStringFinal);
    	if(debug) System.out.println("msgFinal: " + msgStringFinal.length());
        
	   
    	if(debug) System.out.println("Encrypt duration:  " + durEnc/1000 + " us");
    	if(debug) System.out.println("decrypt duration:  " + durDec/1000 + " us");
    	if(debug) System.out.println("reEcrypt duration: " + durReEnc/1000 + " us");
    	
    	rkCoefsWriter.close();
    	cipherCoefsWriter.close();
    	
    	if (Arrays.equals(m.coeffs, m2.coeffs)) {
    		if(debug) System.out.println("Test 2 OK!");
            return 0;
        } else {
        	if(debug) System.out.println("Test 2 Failed!");
            return 1;
        }   
    }
}
