import { useState } from "react";
import Card from "../Card";
import SmallCards from "./SmallCards";
import NameTag from "./NameTag";
import Cards from "../Cards";


export default function Player({jmeno,postava = "TESTOVACI2",pocetKaret = 8,pocetZivotu = 0}:{jmeno:string,postava?:string,pocetKaret?:number,pocetZivotu?:number}) {
    const [pocetKaret1, setPocetKaret] = useState<number>(pocetKaret);

    return (
        
            <div style={{display:"flex",flexDirection:"column"}}>
                <div style={{ display: "flex" , justifyContent:"center"}}>
                    <div>
                    <NameTag jmeno={jmeno} />
                    <div style={{display:"flex",flexDirection:"row"}}>
                        <Card name={"postava:" + postava.toLowerCase()} image={`/img/karty/postavy/${postava}.png`} />
                        <Card name={`${pocetZivotu} životů.`} image={`/img/velkeZivoty/${pocetZivotu}zivoty.png`} />
                    </div>
                    </div>
               
                    <SmallCards count={pocetKaret1} />
                </div>
                <div
                    style={{ transform: "scale(0.7)", transition: "transform 0.2s" }}
                    onMouseEnter={e => (e.currentTarget.style.transform = "scale(1.1)")}
                    onMouseLeave={e => (e.currentTarget.style.transform = "scale(0.7)")}
                >
                    <Cards isRotated={false} />
                </div>
            </div>
    );
}