import Player from "./Player";

export default function Players() {
    return (
        <div style={{
                flexShrink: 0,
                flexDirection: "row",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                padding:"0 10px"
            }}>
                <Player jmeno="honza" />
                <Player jmeno="Někdo, kdo má hodně dlouhé jméno" />
                <Player jmeno="František" />
            </div>
    );
}