import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Contract;



import java.nio.file.Path;
import java.nio.file.Paths;

//This file contains the main method and will instantiate some needed components
public class EVoteApp {

    public static void main(String[] args) throws Exception {
        //Create local path for a wallet
        Path walletPath = Paths.get("wallet");

        //Create the wallet
        Wallet wallet = Wallet.createFileSystemWallet(walletPath);

        //Create path for the network config (utilizing the test-network from the Fabric samples)
        Path networkConfigPath = Paths.get("..", "..", "test-network", "connection-org1.yaml");

        //Load the ccp-template from the test-network Fabric samples
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "user1").networkConfig(networkConfigPath).discovery(true);

        //Initiate the gateway connection
        try (Gateway gateway = builder.connect()) {

            //Retrieve the network and contract through Hyperledger
            Network network = gateway.getNetwork("channel1");
            Contract contract = network.getContract("eVoteContract");

            //Querying all the available regions
            byte[] result = contract.evaluateTransaction("queryAllRegions");
            System.out.println("Querying all regions: " + new String(result));

            //Submitting a transaction that creates a new region along with a candidate
            contract.submitTransaction("createNewRegion", "Region 1", "ExampleRegion", "ExampleCandidate");

            //We query the ExampleRegion created above and print the returned response
            result = contract.evaluateTransaction("queryRegion", "ExampleRegion");
            System.out.println("Querying for a specific region: " + new String(result));
        }
    }
}
