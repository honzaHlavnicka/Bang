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
                <Player jmeno="pepasdsjhdgshuaghsdgahsjgdjhasgdhjhfgj" />
                <Player jmeno="krokouš" />
            </div>
    );
}