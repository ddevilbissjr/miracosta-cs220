@7
    D=M
@R1
    M=D
@11
    D=M
@R2
    M=D
@5
    D=M
@R3
    M=D

(LOOP)
    @R1         //if (R1 >= R2)
        D=M
    @R2
        D=D-M
    @IF_BLOCK   //goto IF_BLOCK if true
    D;JGE

    @R2         //else if (R2 >= R3)
        D=M
    @R3
        D=D-M
    @ELSE_IF    //goto ELSE_IF if true
    D;JGE

    @R0         //else
    D=M

(IF_BLOCK)
    @R1         //if (R1 >= R3)
        D=M
    @R3
        D=D-M
    @IF_FIRST   //goto IF_BLOCK if true
    D;JGE

    @R0         //else
    D=M

(IF_FIRST)
    @R0
        D=M
    @END

(IF_SECOND)
    @R0
        D=M
    @END

(ELSE_IF)
    @R0
        D=M
    @END