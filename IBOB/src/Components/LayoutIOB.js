import React from 'react'
import HeaderIOB from './HeaderIOB'
import Footer from './Footer'
import { Outlet } from 'react-router-dom'

function LayoutIOB() {
    return (
        <div className="dashboard" style={{marginTop:"3%", marginBottom:"5%"}}>
            <HeaderIOB />
            <Outlet />
            <Footer />
        </div>
    )
}

export default LayoutIOB