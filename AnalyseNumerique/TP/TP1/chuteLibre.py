from tkinter import *
import numpy as np
H=W=750
# coordonnees initiales
x0, y0 = H/2, 600
V0=100 # vitesse initiale
h=0.1  # 'pas' du temps
z=np.array([y0,-V0])
# Equation différentielle: z'= f(t,z)
def f(z):
    return np.array([z[1],9.81])

def Euler():
    global z
    z=z+h*f(z)
    # deplacement de la balle a la nouvelle position
    can1.coords(balle, x0, z[0],x0 + 30, z[0] + 30)
    # La fenetre fen1 est actualisee en executant la
    # fonction Euler toutes les 10 millisecondes
    fen1.after(10, Euler)

# ========== Programme principal =============
# Creation de la fenetre principale :
fen1 = Tk()
fen1.title("Probleme de tir")
# creation du canvas :

can1 = Canvas(fen1, bg='dark grey', height=H, width=W)
can1.pack()
# creation de la balle
balle = can1.create_oval(x0, y0, x0 + 30, y0 + 30, width=2, fill='red')
# Lancement de la fonction Euler
Euler()
# demarrage de la boucle principale:
fen1.mainloop()