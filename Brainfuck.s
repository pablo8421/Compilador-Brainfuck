.globl main
main:
LDR R4 ,= _array
push {lr}
pop {pc}

.data
.align 4

_char: .asciz "%c"

_array: .space 30000
