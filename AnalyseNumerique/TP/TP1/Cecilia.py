from tkinter import *
import numpy as np

# coordonnees initiales
x0, y0 = 10, 200
# vitesse initiale
alpha = np.pi / 3
V0 = 50
# 'pas' du temps
h = 0.1
z = np.array([x0, y0, V0 * np.cos(alpha), -V0 * np.sin(alpha)])


def f(z):
    # On ajoute des conditions pour inverser la vitesse de la balle lorsqu'elle atteint les bords de la fenêtre
    if z[0] < 0 or z[0] > W - 30:
        z[2] *= -1
    if z[1] < 0 or z[1] > H - 30:
        z[3] *= -1
    return np.array([z[2], z[3], 0, 9.81])


def Heun():
    global z
    z = z + (h / 2) * (f(z) + f(z + h * f(z)))
    # deplacement de la balle a la nouvelle position
    can1.coords(balle, z[0], z[1], z[0] + 30, z[1] + 30)
    # La fenetre fen1 est actualisee en executant la
    # fonction Heun toutes les 10 millisecondes
    fen1.after(10, Heun)

# ========== Programme principal =============
# Creation de la fenetre principale :
fen1 = Tk()
fen1.title("Probleme de tir")
# creation du canvas :
H = W = 750
can1 = Canvas(fen1, bg='dark grey', height=H, width=W)
can1.pack()
# creation de la balle
balle = can1.create_oval(x0, y0, x0 + 30, y0 + 30, width=2, fill='red')
# Lancement de la fonction Heun
Heun()
# demarrage de la boucle principale:
fen1.mainloop()
