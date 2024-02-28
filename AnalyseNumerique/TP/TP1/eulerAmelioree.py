import numpy as np
import matplotlib.pyplot as plt
def f(x):
    N=1912/5-((325*x+450)*np.cos(x)+(65*x-235)*np.sin(x))*np.exp(-x/5)
    D=338*(x+1)**2
    return N/D
x=np.linspace(0,15,100)
plt.plot(x,f(x))
plt.show()

def F(t,y):
    return (np.sin(t)*np.exp(-t/5)-2*y)/(t+1)

def Phi(t,y,h):
    return F(t+h/2,y+h/2*F(t,y))

def eulerAmelioree(Phi,a,b,y0,n):
    h=(b-a)/n
    y=np.zeros(n+1)
    y[0]=y0
    t = np.linspace(a, b, n+1)
    for k in range(n):
        y[k+1]=y[k]+h*Phi(t[k],y[k],h)
    return t,y



X,Y = eulerAmelioree(Phi,0,10,-1/5,20)
x=np.linspace(0,15,100)
y=f(x)
plt.plot(x,f(x),color='cornflowerblue') # courbe exacte
plt.plot(X,Y,color='orange')    # solution approchée
plt.show()