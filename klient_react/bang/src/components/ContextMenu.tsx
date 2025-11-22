export type ContextMenuOpotions = {text:string,callback?:()=>void,type?:"IMPORTANT"|"DANGER"|"DISABLED"|"NORMAL"|"HEADING"}[]
export default function ContextMenu({x,y,options}:{x:number,y:number,options:ContextMenuOpotions}) {
    //todo: darkmode
    return (
        <div style={{borderRadius:10,backgroundColor:"whitesmoke",padding:4,display:"flex",flexDirection:"column",position:"fixed",top:y,left:x,zIndex:2000,minWidth:150}}>
            {options.map(((value)=>{
                if(value.type === "HEADING"){
                    return <h4 style={{textAlign:"center",margin:3}}>{value.text}</h4>
                }
                return (
                <button style={{background:(value.type!="IMPORTANT"?"whitesmoke":"lightblue"),borderRadius:7,fontSize:"1em",border:(value.type != "DANGER" ? "2px solid grey" : "2px solid red"),padding:7,margin:1,cursor:(value.type!="DISABLED" ? "pointer" : "not-allowed")}} key={value.text}>{value.text}</button>
            )
            }))}
            
        </div>
    );
}