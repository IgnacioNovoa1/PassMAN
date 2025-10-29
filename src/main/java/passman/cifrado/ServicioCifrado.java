package passman.cifrado;
import java.nio.ByteBuffer;
import java.util.Base64;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;

import passman.config.Config;

public class ServicioCifrado{
    private final AWSKMS clienteKMS;
    private final String keyArn;

    public ServicioCifrado(){
        BasicAWSCredentials credenciales = new BasicAWSCredentials("test", "test");

        AwsClientBuilder.EndpointConfiguration configEndpoint = new AwsClientBuilder.EndpointConfiguration(Config.KMS_ENDPOINT, Config.KMS_REGION);
        this.clienteKMS = AWSKMSClientBuilder.standard().withEndpointConfiguration(configEndpoint).withCredentials(new AWSStaticCredentialsProvider(credenciales)).build();
        this.keyArn = Config.KMS_KEY_ARN;
    }
    public String cifrar(String texto){
        ByteBuffer textoBuffer = ByteBuffer.wrap(texto.getBytes());
        EncryptRequest req = new EncryptRequest().withKeyId(this.keyArn).withPlaintext(textoBuffer);
        ByteBuffer textoCifradoBuffer = clienteKMS.encrypt(req).getCiphertextBlob();
        return Base64.getEncoder().encodeToString(textoCifradoBuffer.array());
    }

    public String descifrar(String textoCifrado64){
        byte[] cifradoBytes = Base64.getDecoder().decode(textoCifrado64);
        ByteBuffer textoCifradoBuffer = ByteBuffer.wrap(cifradoBytes);
        DecryptRequest req = new DecryptRequest().withCiphertextBlob(textoCifradoBuffer).withKeyId(this.keyArn);
        ByteBuffer textoBuffer = clienteKMS.decrypt(req).getPlaintext();
        return new String(textoBuffer.array());
    }
}
