import { createPortal } from "react-dom";
import { useZoom } from "../modules/ZoomContext";

export default function ZoomDialog() {
    const {zoomedCard ,setZoomedCard} = useZoom()
    return createPortal(
        <div onClick={()=>{setZoomedCard(null)}}  style={{
            position: "fixed",
            top: 0,
            left: 0,
            width: "100vw",
            height: "100vh",
            backgroundColor: "rgba(0, 0, 0, 0.36)",
            display: zoomedCard!==null ? "flex" : "none",
            alignItems: "center",
            justifyContent: "center",
            backdropFilter: "blur(3px)",
            zIndex: 1001,
            cursor:"zoom-out"}}>
                <div style={{
                    maxWidth: "800px",
                    maxHeight: "90vh",
                    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)",
                    borderRadius: "8px",
                    overflow: "auto",
                    border: "4px solid white",
                    backgroundColor: "white",
                    cursor:"auto",
                }}>
                    <div style={{position:"sticky",top:0,backgroundColor:"white",padding:"4px 8px",borderBottom:"1px solid #ccc",fontWeight:"bold",textAlign:"center"}}>
                        Přiblížená karta (kliknutím mimo kartu zavřít)

                    </div>
                    {zoomedCard && <img src={zoomedCard} alt="Zoomed Card" style={{ width: "100%", height: "auto", display: "block" }} />}
                </div>
        </div>
        ,document.body
    )
}