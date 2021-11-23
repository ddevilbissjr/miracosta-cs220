@-1
    D=M
@R0
    M=D
@5
    D=M
@size
    M=D
@i
    M=1         // i = 1

(LOOP)
    @i
        D=M
    @size
        D=D-M
    @STOP       // if i > n goto STOP
    D;JLT

    @i
        D=M
    @R0
        D=D-M
    @IF_BLOCK
    D;JGT

@LOOP
0;JMP

(STOP)
    @sum
        D=M
    @R1
        M=D     // RAM[1] = sum

(IF_BLOCK)
@R0
    D=M
@END

(END)
    @END
    0;JMP