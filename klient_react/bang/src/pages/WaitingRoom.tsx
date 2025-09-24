import FlippingCard from "../components/FlippingCard";

export default function WaitingRoom({ children }: { children: React.ReactNode }) {
    return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '80vh' }}>
           <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',borderRadius: '8px',padding: '20px' }}>
                <FlippingCard pauseOnHover={false} frontImage={["/img/karty/bang.png"]} backImage={"/img/karty/zezadu.png"}  width={110}/>

                {/*<div className="spinner" style={{
                width: 70,
                height: 70,
                border: '6px solid #ccc',
                borderTop: '6px solid #1976d2',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite'
                }} />*/}
                <div style={{ marginTop: 24, fontSize:  20, backgroundColor: '#918080', padding: 16, borderRadius: 8, boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                {children}
                </div>
                <style>
                {`
                    @keyframes spin {
                        0% { transform: rotate(0deg); }
                        100% { transform: rotate(360deg); }
                    }
                    h1 {
                        font-size: 17px;
                        font-weight: bold;
                    }
                    `}
                </style>
            </div>
        </div>
    );
}