import { useState } from "react";
import Card from "../Card";
import SmallCards from "./SmallCards";
import NameTag from "./NameTag";


export default function Player({jmeno,postava = "TESTOVACI",pocetKaret = 3,pocetZivotu = 0}:{jmeno:string,postava?:string,pocetKaret?:number,pocetZivotu?:number}) {
    const [pocetKaret1, setPocetKaret] = useState<number>(pocetKaret);

    return (
        
            <div >
                <div style={{ display: "flex" , justifyContent:"center"}}>
                    <div>
                    <NameTag jmeno={jmeno} />
                    <div style={{display:"flex",flexDirection:"row"}}>
                        <Card image={"/img/karty/postavy/"+postava+".png"} />
                        <Card image={"/img/velkeZivoty/"+pocetZivotu+"zivoty.png"} />
                    </div>
                    </div>
               
                    <SmallCards count={pocetKaret1} />
                </div>
            </div>
    );
}