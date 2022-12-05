import java.util.ArrayList;
import java.util.List;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import com.owlike.genson.Genson;


//We use hyperledger's annotations to express out intent

@Default
public final class RegionChaincode implements ContractInterface {

    //Using Genson Json schema generator
    private final Genson genson = new Genson();

    private enum RegionErrors {REGION_NOT_FOUND, REGION_ALREADY_EXISTS}

    //Queries the ledger for a region with the specific key
    @Transaction()
    public Region queryRegion(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String regionState = stub.getStringState(key);
        if (regionState.isEmpty()) {
            String missingRegionError = String.format("Region %s does not exist", key);
            System.out.println(missingRegionError);
            throw new ChaincodeException(missingRegionError, RegionErrors.REGION_NOT_FOUND.toString());
        }
        Region region = genson.deserialize(regionState, Region.class);
        return region;
    }

    //Creates a number of initial regions that autoincrement e.g. "Region 001" (zero-filled if digit is missing)
    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String[] regionData = {"{ \"name\": \"ExampleRegion\", \"elective\": \"Candidate\"}"};
        for (int i = 0; i < regionData.length; i++) {
            String key = String.format("Region %02d", i);
            Region region = genson.deserialize(regionData[i], Region.class);
            String regionState = genson.serialize(region);
            stub.putStringState(key, regionState);
            System.out.println("success");
        }
    }

    //Invoke to create new regions on the ledger through a transaction
    @Transaction()
    public Region createNewRegion(final Context ctx, final String key, final String name, final String elective) {
        ChaincodeStub stub = ctx.getStub();
        String regionState = stub.getStringState(key);
        if (!regionState.isEmpty()) {
            String errorMessage = String.format("****ERROR*** Region %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, RegionErrors.REGION_ALREADY_EXISTS.toString());
        }

        Region region = new Region(name, elective);
        regionState = genson.serialize(region);
        stub.putStringState(key, regionState);
        return region;
        System.out.print("foo");
    }


    //Query all regions on the ledger and save them to a new array
    @Transaction()
    public Region[] queryAllRegions(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        List<Region> regionsList = new ArrayList<Region>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("Region 0", "Region 99");
        for (KeyValue result : results) {
            Region region = genson.deserialize(result.getStringValue(), Region.class);
            regions.add(region);
        }
        //Save regions to a new array and return it
        Region[] response = regionsList.toArray(new Region[regionsList.size()]);
        return response;
    }
}