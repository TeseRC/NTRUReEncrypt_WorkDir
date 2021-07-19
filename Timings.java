
package nics.crypto.ntrureencrypt;

import java.util.Arrays;
import net.sf.ntru.encrypt.EncryptionKeyPair;
import net.sf.ntru.encrypt.EncryptionParameters;
import net.sf.ntru.polynomial.IntegerPolynomial;

import net.sf.ntru.encrypt.NtruEncrypt;

public class Timings {
    private static final int NUM_ENC_KEY_GEN = 10;
    private static final int NUM_ENCRYPT = 50;
    private static final int NUM_DECRYPT = 50;
    private static final int NUM_REENCRYPT = 50;
    
    
    private IntegerPolynomial cA, cB;
    private EncryptionKeyPair kpA, kpB;
    private ReEncryptionKey rk;
    private IntegerPolynomial m, m2;
    private long t1, t2, tres;
    
    
    private void run() throws Exception {
        long minEncKeyGenTime = Long.MAX_VALUE;
        long minReEncKeyGenTime = Long.MAX_VALUE;
        long minEncryptTime = Long.MAX_VALUE;
        long minDecryptTime = Long.MAX_VALUE;
        long minReEncryptTime = Long.MAX_VALUE;
        long minTotalTime = Long.MAX_VALUE;
        long totEncKeyGenTime = 0;
        long totReEncKeyGenTime = 0;
        long totEncryptTime = 0;
        long totDecryptTime = 0;
        long totReEncryptTime = 0;
        long totTotalTime = 0;
        int iterations = 5;
        
        NTRUReEncrypt ntruReEncrypt = new NTRUReEncrypt(EncryptionParameters.EES1171EP1_FAST);
        
        
        // we use a message bigger than the allowed by the NTRUReEncrypt system
        // the message extra bytes are discarded 
        // in Tese1 we can observe the maximum message size allowed 
        // by the different parameters
        String msg1 = "The quick brown fox ##";
        String msg =
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipiccsicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectddwetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipisicing elit, sed do eiusmod temporu" +
                "Lorem ipsum dolor sit et, consectetur adipiccsicing elit, sed do eiusmod temporu" +
                "##";
        
        System.out.println("the message size is: " + msg.length() + "B");
    	m = ntruReEncrypt.msg2trin(msg.getBytes());
    	//m = ntruReEncrypt.message(new byte[]{122,2,56});
        
      
        System.out.printf(" %10dx %10dx   %10dx%10dx%10dx", NUM_ENC_KEY_GEN, NUM_ENC_KEY_GEN, NUM_ENCRYPT, NUM_DECRYPT, NUM_REENCRYPT);
        System.out.println();
        System.out.println("   EncKeyGen    ReEncKeyGen    Encrypt    Decrypt    ReEncrypt    Total");
        System.out.println();
        
        for (int i=0; i<iterations; i++) {
            long encKeyGenTime = encKeyGenBench(ntruReEncrypt);
            System.out.print("   " + formatDuration(encKeyGenTime/NUM_ENC_KEY_GEN) + "  ");
            long reEncKeyGenTime = reEncKeyGenBench(ntruReEncrypt);
            System.out.print("   " + formatDuration(reEncKeyGenTime/NUM_ENC_KEY_GEN) + "  ");
            long encryptTime = encryptBench(ntruReEncrypt);
            System.out.print(formatDuration(encryptTime/NUM_ENCRYPT) + "  ");
            long reEncryptTime = reEncryptBench(ntruReEncrypt);
            System.out.print(formatDuration(reEncryptTime/NUM_REENCRYPT) + "  ");
            long decryptTime = decryptBench(ntruReEncrypt);
            System.out.print(formatDuration(decryptTime/NUM_DECRYPT) + "  ");
            long totalTime = encKeyGenTime/NUM_ENC_KEY_GEN + reEncKeyGenTime/NUM_ENC_KEY_GEN + encryptTime/NUM_ENCRYPT + decryptTime/NUM_DECRYPT + reEncryptTime/NUM_REENCRYPT;
            System.out.println(formatDuration(totalTime));
            
            minEncKeyGenTime = Math.min(encKeyGenTime/NUM_ENC_KEY_GEN, minEncKeyGenTime);
            minReEncKeyGenTime = Math.min(reEncKeyGenTime/NUM_ENC_KEY_GEN, minReEncKeyGenTime);
            minEncryptTime = Math.min(encryptTime/NUM_ENCRYPT, minEncryptTime);
            minDecryptTime = Math.min(decryptTime/NUM_DECRYPT, minDecryptTime);
            minReEncryptTime = Math.min(reEncryptTime/NUM_REENCRYPT, minReEncryptTime);
            minTotalTime = Math.min(totalTime, minTotalTime);
            // first round is warm up
            if (i > 0) {
                totEncKeyGenTime += encKeyGenTime/NUM_ENC_KEY_GEN;
                totReEncKeyGenTime += reEncKeyGenTime/NUM_ENC_KEY_GEN;
                totEncryptTime += encryptTime/NUM_ENCRYPT;
                totDecryptTime += decryptTime/NUM_DECRYPT;
                totReEncryptTime += reEncryptTime/NUM_REENCRYPT;
                totTotalTime += totalTime;
            }
        }
        System.out.println();
        System.out.println("Min" + formatDuration(minEncKeyGenTime) + "     " + formatDuration(minReEncKeyGenTime) + "  " + formatDuration(minEncryptTime) + "  " + formatDuration(minDecryptTime) + "  " + formatDuration(minReEncryptTime) + "  " + formatDuration(minTotalTime));
        iterations--;   // don't count warm up round
        
        long avgEncKeyGenTime = totEncKeyGenTime / iterations;
        long avgReEncKeyGenTime = totReEncKeyGenTime / iterations;
        long avgEncryptTime = totEncryptTime / iterations;
        long avgDecryptTime = totDecryptTime / iterations;
        long avgReEncryptTime = totReEncryptTime / iterations;
        long avgTotalTime = totTotalTime / iterations;
        
        System.out.println("Avg" + formatDuration(avgEncKeyGenTime) + "     " + formatDuration(avgReEncKeyGenTime) + "  " + formatDuration(avgEncryptTime) + "  " + formatDuration(avgDecryptTime) + "  " + formatDuration(avgReEncryptTime) + "  " + formatDuration(avgTotalTime));
        System.out.println("Ops" + formatOpsPerSecond(avgEncKeyGenTime, NUM_ENC_KEY_GEN) + "     " + formatOpsPerSecond(avgReEncKeyGenTime, NUM_ENC_KEY_GEN) + "  " + formatOpsPerSecond(avgEncryptTime, NUM_ENCRYPT) + "  " + formatOpsPerSecond(avgDecryptTime, NUM_DECRYPT) + "  " + formatOpsPerSecond(avgReEncryptTime, NUM_REENCRYPT));
    }
    
    /**
     * 
     * @param duration time it took for all <code>numOps</code> operations to complete
     * @param numOps number of operations performed
     * @return
     */
    private String formatOpsPerSecond(long duration, int numOps) {
        double ops = 1000000000.0 / duration;
        return String.format("%7.2f/s", ops);
    }
    
    private String formatDuration(long n) {
        return String.format("%1$7sus", n/1000);
    }
    
    private long encKeyGenBench(NTRUReEncrypt ntruReEncrypt) {
        t1 = System.nanoTime();
        for (int i=0; i<NUM_ENC_KEY_GEN; i++)
        	kpA = ntruReEncrypt.generateKeyPair();
        t2 = System.nanoTime();
        kpB = ntruReEncrypt.generateKeyPair();
        tres = t2 - t1;
        return tres;
    }
    
    
    private long encryptBench(NTRUReEncrypt ntruReEncrypt) throws Exception {
    	
    	
    	t1 = System.nanoTime();
        for (int i=0; i<NUM_ENCRYPT; i++)
            cA = ntruReEncrypt.encrypt(kpA.getPublic(), m);
        t2 = System.nanoTime();
        tres = t2 - t1;
        return tres;
    }
    
    private long reEncKeyGenBench(NTRUReEncrypt ntruReEncrypt) throws Exception {
        t1 = System.nanoTime();
        for (int i=0; i<NUM_ENC_KEY_GEN; i++)
        	rk = ntruReEncrypt.generateReEncryptionKey(kpA, kpB);
        t2 = System.nanoTime();
        tres = t2 - t1;
        return tres;
    }
    
    private long decryptBench(NTRUReEncrypt ntruReEncrypt) throws Exception {
        t1 = System.nanoTime();
        for (int i=0; i<NUM_DECRYPT; i++) {
        	m2 = ntruReEncrypt.decrypt(kpB.getPrivate(), cB);
            if (!Arrays.equals(m.coeffs, m2.coeffs))
            	System.out.println("Decryption failure");
        }
        t2 = System.nanoTime();
        tres = t2 - t1;
        return tres;
    }
    
    private long reEncryptBench(NTRUReEncrypt ntruReEncrypt) throws Exception {
        t1 = System.nanoTime();
        for (int i=0; i<NUM_REENCRYPT; i++)
        	cB = ntruReEncrypt.reEncrypt(rk, cA);
        t2 = System.nanoTime();
        tres = t2 - t1;
        return tres;
    }
    
    
    public static void main(String[] args) throws Exception {
        new Timings().run();
    }
}