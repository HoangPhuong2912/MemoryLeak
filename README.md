# MemoryLeak
MemoryLeak
Operation hint: Click the red button to generate a leaked object, and then press the return key to close the current interface. When the interface is destroyed, these memory objects have not been destroyed, so a memory leak occurs.
Solution hint: All memory leaks are caused by forgetting to release the generated objects. The simple solution is to release the useless memory when the interface is destroyed.
