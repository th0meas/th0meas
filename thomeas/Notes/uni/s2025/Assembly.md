#Assembly [[OAYS]] 

**Source**: https://yassinebridi.github.io/asm-docs/asm_tutorial_01.html
## Part 1

Inside the CPU
 ![[cpu.gif]]

# **General Purpose Registers**
*8086 CPU has 8 general purpose registers, each register has its own name:*
- **AX** - the accumulator register (divided into **AH / AL**).
- **BX** - the base address register (divided into **BH / BL**).
- **CX** - the count register (divided into **CH / CL**).
- **DX** - the data register (divided into **DH / DL**).
- **SI** - source index register.
- **DI** - destination index register.
- **BP** - base pointer.
- **SP** - stack pointer.


The 4 general purpose registers (AX, BC, CX, DX) are made of two separate 8-bit registers, for example if AX = ==00110000==00111001b then AH = ==00110000==b and AL = 00111001b. So, modifying the 8 bit register, the 16 bit register is also updated and vise-versa. H stands for high and L is for low part.

Because registers are located inside the CPU, they are much faster than memory. Accessing a memory location requires the use of a system bus, so it takes much longer. Therefore, try to keep variables in the registers. 

# Segment Registers
  
- **CS** - points at the segment containing the current program.
- **DS** - generally points at segment where variables are defined.
- **ES** - extra segment register, it's up to a coder to define its usage.
- **SS** - points at the segment containing the stack.

Purpose of segment registers: **Point at accessible blocks of memory**
They work together with general purpose registers to access any memory value. For example, if we would like to access memory at the physical address 12345h (hexadecimal), we should set the 
DS = 1230h and SI = 0045h. This is good, since this way we can access much more memory than with a single register that is limited to 16 bit values.

- BX, SI, DI --> DS
- BP, SP --> SS
*The address formed with 2 registers is called an **effective address**.*  
*By default **BX, SI** and **DI** registers work with **DS** segment register;*            
***BP** and **SP** work with **SS** segment register.*  
*Other general purpose registers cannot form an effective address!*  
*also, although **BX** can form an effective address, **BH** and **BL** cannot.*

# Special Purpose Registers

- **IP** - the instruction pointer
- **flags register** - determines the current state of the microprocessor.
IP register always works with CS segment register and it points to **currently executing instruction**.
**Flags register** is modified automatically by CPU after mathematical operations, this allows to determine the type of the result, and to determine conditions to transfer control to other parts of the program.  
Generally you cannot access these registers directly, the way you can access AX and other general registers, but it is possible to change values of system registers using some tricks that you will learn a little bit later.

## Part 2

