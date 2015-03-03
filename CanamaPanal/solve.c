#include <stdio.h>
#include <stdlib.h>

#define TableSize 100003

/*Position data type.*/
typedef struct pnode {
  struct pnode *next, *back; /*position from which this one came*/
  char piece, board[12], dir; /*piece that moved to this position*/
  int last, biggestHash, totalNumber, cost;
}pnode;

/*hash table structure*/
typedef struct hTable{
  pnode *table[100003];
  int biggestHash, totalNumber, emptyBuckets, oneBuckets;
} hTable;

/*QUEUE*/
typedef struct queue{
   int first, last, size, capacity, max;
   pnode *array[50000];
}queue;

/*create a hash table*/
hTable* createHT(){
  hTable *ht = (hTable *) malloc(sizeof(hTable));
  *ht->table = (pnode *) malloc(sizeof(pnode) * 50000);
  ht->totalNumber = 0;
  ht->biggestHash = 0;
  ht->emptyBuckets = 100003;
  ht->oneBuckets = 0;
  int i = 0;
  while(i<100003){
    ht->table[i] = NULL;
    i++;
  }
  return ht;
}

/*make a copy of the 'board'*/
void boardCopy (char * a, char * b){
  int i = 0;
  while(i <12){
    a[i] = b[i];
    i++;
  }
}

/*create a new board position*/
pnode* NEWPOSITION(){
  pnode *p = (pnode *) malloc(sizeof(pnode));
  if(p==NULL){
    printf("Malloc failed.");
    exit(1);
  }
  return p;
}

/*compare two boards to see if equivalent, return 1 if they are*/
int boardComp (char * a, char * b){
  int i = 0;
  while(i <12){
    if(a[i] != b[i])
      return 0;
    i++;
  }
  return 1;
}

/*HASH FUNCTIONS*/
/*calculate hash function*/
int hash(pnode *currPosit) {
  int charIndex = 0;
  unsigned int hashValue = 0;
  while (charIndex < 12) {
      hashValue = ((hashValue * 128) + (int) currPosit->board[charIndex++]) % 100003;
    }
  return hashValue;
}

/*GAME MOVES*/
/*find the blank position in a board*/
int blankPosition(char * a){
  int i;
  for(i = 0; i < 12; i++){
    if(a[i] == ' ')
      return i;
  }
  return -1;
}

/*print out the board*/
void showBoard(char * a){
  int i = 0;
  while(i <12){
    printf("%c  ", a[i]);
    if(i == 5)
      printf("\n");
    i++;
  }
  printf("\n\n");
}

/*initialize a new circular queue array*/ 
queue *NEWQUEUE(int inp) {
   queue *q = malloc(sizeof(queue));
   q->first = 0;
   q->last = 0;
   q->size = 0;
   q->capacity = inp;
   q->max = 0;
   *q->array = malloc(50000 * sizeof(pnode));
  return q;
}

/*add a new value to the queue*/
void ENQUEUE(pnode *a, queue *q) {
  if(q->size == q->capacity){
    printf("queue overflow.");
    return;
  }
  q->array[q->first] = a;
  q->first++;
  q->size++;
  if(q->size > q->max)
    q->max = q->size;
  if(q->first == (q->capacity - 1))
    q->first = 0;
}

/*the dequeue function*/
pnode DEQUEUE(queue *q) {
  if(q->size == 0){
    printf("stack underflow");
  }
  pnode *a = q->array[q->last];
  q->array[q->last] = NULL;
  q->last++;
  q->size--;
  if(q->last == (q->capacity - 1))
    q->last = 0;
   return *a;
}

/*MORE HASH FUNCTIONS*/
/*checks if current board is in hash table*/
int MEMBER(pnode *p, hTable *ht, int h){
  pnode *temp = ht->table[h];
  if(temp==NULL){
    return 0;
  }
  else{
    while(temp!=NULL){
      if(boardComp(p->board, temp->board) == 1){
        return 1;
      }
      temp = temp->next;
    }
    return 0;
  }
}

/*add to hashtable function*/
void INSERT(pnode *p, hTable *ht, int h, queue *q){
  int i = 2;
  if(MEMBER(p, ht, h) == 1){
    free(p);
    return;}
  if(ht->table[h] == NULL){
    ht->table[h] = p;
    ht->emptyBuckets--;
    ht->oneBuckets++;
  }
  else{
    pnode *temp = ht->table[h];
    if(temp!=NULL){
      while(temp->next!=NULL){
      temp = temp->next;
      i++;
      }
    temp->next = p;}}
    if(i > ht->biggestHash)
      ht->biggestHash = i;
    ht->totalNumber++;
  ENQUEUE(p, q);
}

/*make an individual move on the game board and then insert into hash table*/
void singleMove(pnode * a, int i, int j, queue *q, hTable *ht, char d){  
  if(a->last != j){
      pnode *b = NEWPOSITION();
      boardCopy(b->board, a->board);
      b->back = a;
      b->piece = b->board[j];
      b->cost = a->cost + 1;
      char temp;
      temp = b->board[i];
      b->board[i] = b->board[j];
      b->board[j] = temp;
      b->last = i; 
      b->next = NULL;
      b->dir = d;
      int k = hash(b);
      INSERT(b, ht, k, q);
      }
}

/*manages all the different moves a board could make*/
void move(pnode * a, queue *q, hTable *ht){
  int i = blankPosition(a->board);
  int j = -1;
  if(i == 11){
    singleMove(a, i, 10, q, ht, 'W');
    singleMove(a, i, 5, q, ht, 'N');
    }
  else if(i>6){
    j = i + 1;
    singleMove(a, i, j, q, ht, 'E');
    j = i - 6;
    singleMove(a, i, j, q, ht, 'N');
    j = i - 1;
    singleMove(a, i, j, q, ht, 'W');
  }
  else if(i == 6){
    singleMove(a, i, 7, q, ht, 'E');
    singleMove(a, i, 0, q, ht, 'N');
  }
  else if(i == 5){
    singleMove(a, i, 11, q, ht, 'S');
    singleMove(a, i, 4, q, ht, 'E');
  }
  else if(i>0){
    j = i + 6;
    singleMove(a, i, j, q, ht, 'S');
    j = i - 1;
    singleMove(a, i, j, q, ht, 'W');
    j = i + 1;
    singleMove(a, i, j, q, ht, 'E');
  }
  else if(i == 0){
    singleMove(a, i, 1, q, ht, 'E');
    singleMove(a, i, 6, q, ht, 'S');
  }
}

/*check to see if move on top of queue is correct*/
int finalCheck(char * a){
  char final[12] = {'P', 'A', 'N', 'A', 'M', 'A', 'C', 'A', 'N', 'A', 'L', ' '};
  int i = 0;
  while(i < 12){
    if(a[i] != final[i])
      return 0;
    i++;
  }
  return 1;
}

/*initialize the first move*/
pnode* firstMOVE(){
  pnode *p = NEWPOSITION();
  char gameBoard[12] = {'C', 'A', 'N', 'A', 'M', 'A', 'P', 'A', 'N', 'A', 'L', ' '};
  boardCopy(p->board, gameBoard);
  p->cost = 0;
  p->piece = ' ';
  p->last = -1;
  p->back = NULL;
  p->next = NULL;
  return p;
}

/*prints out the solution in reverse out...USING RECURSION*/
void recursiveShow(pnode *p){
  if(p==NULL){
    return;
  }
  recursiveShow(p->back);
  if(p->cost == 0)
    printf("Starting board\n");
  else
    printf("Step %d: Moved %c %c\n", p->cost, p->piece, p->dir);
  showBoard(p->board);
}

/*prints out all the statistics regarding the hashtable*/
void hashStats(hTable *ht){
  printf("Items in hash table: %d\n", ht->totalNumber);
  printf("Empty buckets in the hash table: %d\n", ht->emptyBuckets);
  printf("Number of buckets with only 1 item in it: %d\n", ht->oneBuckets);
  printf("Largest number of items in a single bucket: %d\n", ht->biggestHash);
}

int main (){
  /*initialize all the variables in main*/
  pnode *p = firstMOVE();
  queue *q = NEWQUEUE(50000);
  hTable *table = createHT();
  int i = hash(p);
  table->table[i] = p;
  /*iteratively look for the solution*/
  while(1){
    move(p, q, table);
    i = hash(p);
    p = NEWPOSITION();
    *p = DEQUEUE(q);
    if(finalCheck(p->board) == 1)
      break;
  }
  /*print out the answers*/
  recursiveShow(p);
  printf("\nCompleted in %d steps\n", p->cost);
  hashStats(table);
  printf("There are currently %d items in the queue\n", q->size);
  printf("The max number of items in the queue was %d\n", q->max);
  return 0;
}