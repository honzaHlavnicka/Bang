import { useState } from 'react'

import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
     <span  style={{fontSize:(count*3+15) + "px"}} onClick={() => setCount((count) => count + 1)}> {count != 0 ? `Už jsi kliknul ${count}krát` : `tady se bude spouštět bang aplikace`}</span>
     <div>
      {/* <button style={{fontSize:(count*3+15) + "px"}} onClick={() => setCount((count) => count + 1)}> {count != 0 ? `Už jsi kliknul ${count}krát` : `Klikni!`}</button> */}
     </div>
    </>
  )
}

export default App
