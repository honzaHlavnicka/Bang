import Card from "../Card";
import Cards from "../Cards";
import NameTag from "./NameTag";

 export default function MyThings() {
    const role = "SERIF";
    const jmeno = "honza";
    return (
        <div style={{ width: "100%", display: "flex", flexDirection: "row", justifyContent: "center", alignItems: "center" }}>
            <div style={{ marginRight: "32px" }}>
                <NameTag jmeno={jmeno} />
                <Card image={`/img/karty/role/${role}.png`} />
            </div>
            <div style={{ flex: 1, display: "flex", justifyContent: "center" }}>
                <Cards />
            </div>
        </div>
    );
}