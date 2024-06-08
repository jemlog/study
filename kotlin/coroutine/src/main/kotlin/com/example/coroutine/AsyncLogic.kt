package com.example.coroutine




/*
만약 자식 코루틴의 동작을 cancel 하기 위해서는 자식 코루틴의 영역 내에
suspend 메서드가 있어야 한다. 아니면 협력 안해줌
Dispatcher Default를 넣어야 다른 스레드에서 동작한다
 */
