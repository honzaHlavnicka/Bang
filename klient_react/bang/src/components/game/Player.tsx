import { useState } from "react";
import Card from "../Card";
import SmallCards from "./SmallCards";
import NameTag from "./NameTag";
import Cards from "../Cards";
import {type CardType } from "../../modules/GameContext";


export default function Player({jmeno,postava = "TESTOVACI2",pocetKaret = 8,pocetZivotu = 0,vylozeneKarty=[]}:{jmeno:string,postava?:string,pocetKaret?:number,pocetZivotu?:number,vylozeneKarty?:Array<CardType>|null}) {
    vylozeneKarty = vylozeneKarty ? vylozeneKarty : [];
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
               
                    <SmallCards count={pocetKaret} />
                </div>
                <div
                    style={{ transform: "scale(0.7)", transition: "transform 0.2s" }}
                    onMouseEnter={e => (e.currentTarget.style.transform = "scale(1.1)")}
                    onMouseLeave={e => (e.currentTarget.style.transform = "scale(0.7)")}
                >
                    <Cards cards={vylozeneKarty}  isRotated={false} />
                </div>
            </div>
    );
}