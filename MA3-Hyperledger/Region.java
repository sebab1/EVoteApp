import java.util.Objects;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.contract.annotation.DataType;
import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Region {

    @Property()
    private final String name, elective;

    public Region(@JsonProperty("name") final String name, @JsonProperty("elective") final String elective) {
        this.name = name;
        this.elective = elective;
    }

    //Check if an object already of name/elective already exists
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            System.out.println("No such object");
            return false;
        }
        Region other = (Region) obj;
        return Objects.deepEquals(new String[]{getName(), getElective()}, new String[]{other.getName(), other.getElective()});
    }

    //Return a hash of the name and elective object
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getElective());
    }

    //Formatting with Genson
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [name =" + name + ", elective =" + elective + "]";
    }

    public String getName() {
        return name;
    }

    public String getElective() {
        return elective;
    }
}