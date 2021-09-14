# DocumentStore

Document Store and Search Engine written in Java as part of the Yeshiva College Data Structures course.

Document Store supports TXT and PDF files. 
    PDF functionality leverages the Apache PDFBox library.

Includes ability to restrict data stored in RAM in which case LFU documents are written to disk until retrieved
    Document Serialization is achieved by leveraging the Google Gson library
    Resulting Json is written to disk for storage until retrieved

Documents are stored in assorted data structures built from the ground up: 
    Trie is used for word search
    B-Tree is used for document storage
    Stack is used for undo functionality
    Heap is used for chaching


    
