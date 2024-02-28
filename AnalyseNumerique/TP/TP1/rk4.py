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

def rk4(Phi,a,b,y0,n):
    h=(b-a)/n
    y=np.zeros(n+1)
    y[0]=y0
    t = np.linspace(a, b, n+1)
    for k in range(n):
        K1=F(t[k],y[k])
        K2=F(t[k]+h/2,y[k]+h/2*K1)
        K3 = F(t[k] + h / 2, y[k] + h / 2 * K2)
        K4 = F(t[k] + h , y[k] + h * K3)
        y[k+1]=y[k]+h*(K1+2*K2+2*K3+K4)/6
    return t,y

X,Y = rk4(F,0,10,-1/5,20)
x=np.linspace(0,15,100)
y=f(x)
plt.plot(x,f(x),color='cornflowerblue') # courbe exacte
plt.plot(X,Y,color='orange')    # solution approchée
plt.show()