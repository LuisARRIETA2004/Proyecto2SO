/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.proyecto2so;

import gui.MainFrame;

public class Proyecto2SO {
    public static void main(String[] args) {
        // Esto hace que al darle al botón verde de Play se abra tu interfaz
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
