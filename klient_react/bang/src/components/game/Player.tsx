import { useState } from "react";
import Card from "../Card";
import SmallCards from "./SmallCards";



export default function Player({jmeno,postava,pocetKaret}:{jmeno:string,postava:string,pocetKaret:string}) {
    const [pocetKaret1, setPocetKaret] = useState<number>(pocetKaret);

    return (
            <div style={{ display: "flex", width: "100%" , justifyContent:"center"}}>
                    <span className="jmeno">{jmeno}</span>
                    <div onClick={e=>setPocetKaret(pocetKaret1+1)}>
                        <Card image={"/img/karty/postavy/"+postava+".png"} />
                    </div>
                    
               
                    <SmallCards count={pocetKaret1} />
            </div>
    );
}