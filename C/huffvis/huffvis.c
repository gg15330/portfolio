#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <ctype.h>

#define NUMARGS 2
#define TEXTFILE argv[1]
#define NUMCHARS 128
#define EMPTYNODE -1

struct node {
  int character;
  int count;
  struct node *left;
  struct node *right;
  struct node *next;
};

typedef struct node Node;

void checkArgs(int argc);
void initLeafArray(Node leafarray[]);
void initTreeArr(char **treearr, int numnodes);
void sortedInsert(Node *start, Node *new);
int countCompare(const void *a, const void *b);
int findTotalNodes(Node *start);
int findTreeDepth(Node *t);

void readInFile(FILE *f, Node leafarray[]);
Node *buildLeafList(Node leafarray[]);
Node *buildHuffTree(Node *start);
void assignTreeArr(Node *tree, char **treearr, int x, int y);

Node *createParent(Node *left, Node *right);
Node *allocateNode();
char **allocateTreeArr(int arrsize);
void freeList(Node *start);
void freeTreeArr(char **treearr, int arrsize);

void printList(Node *current);
void printArray(Node leafarray[]);
void printTreeArr(char **treearr, int numnodes);

int main(int argc, char *argv[])
{
  FILE *fp;
  Node *start, *root;
  Node leafarray[NUMCHARS];
  char **treearr;
  int x = 0;
  int y = 0;
  int numnodes = 0;
  int arrsize = 0;

  checkArgs(argc);
  initLeafArray(leafarray);
  fp = fopen(TEXTFILE, "r");
  readInFile(fp, leafarray);
  fclose(fp);
  start = buildLeafList(leafarray);
  root = buildHuffTree(start);
  numnodes = findTotalNodes(start);
  arrsize = (numnodes*numnodes);
  treearr = allocateTreeArr(arrsize);
  initTreeArr(treearr, numnodes);
  assignTreeArr(root, treearr, x, y);
  printTreeArr(treearr, numnodes);
  freeList(start);
  freeTreeArr(treearr, numnodes);

  return 0;
}

void checkArgs(int argc)
{
  if(argc != NUMARGS) {
    fprintf(stderr, "Incorrect number of arguments.\n");
    exit(1);
  }
}

void initLeafArray(Node leafarray[])
{
  int i = 0;

  for(i = 0; i < NUMCHARS; i++) {
    leafarray[i].character = i;
    leafarray[i].count = 0;
    leafarray[i].left = NULL;
    leafarray[i].right = NULL;
    leafarray[i].next = NULL;
  }
}

void initTreeArr(char **treearr, int numnodes)
{
  int i, j = 0;
  for(i = 0; i < numnodes; i++) {
    for(j = 0; j < numnodes; j++) {
      treearr[i][j] = ' ';
    }
  }
}

void sortedInsert(Node *current, Node *new)
{
  while(current->next && (new->count > current->count)) {
    current = current->next;
  }

  new->next = current->next;
  current->next = new;
}

int countCompare(const void *a, const void *b)
{
  Node *ia = (Node *)a;
  Node *ib = (Node *)b;
  return ((ia)->count - (ib)->count);
}

int findTotalNodes(Node *start)
{
  int count = 0;

  while(start) {
    count++;
    start = start->next;
  }
  return count;
}

/*function adapted from http://www.geeksforgeeks.org/write-a-c-program-to-find-the-maximum-depth-or-numnodes-of-a-tree/ */
int findTreeDepth(Node *t)
{
  int ld, rd = 0;

  if(!t) { return 0; }
  else {
    ld = findTreeDepth(t->left);
    rd = findTreeDepth(t->right);

    if(ld > rd) { return (ld + 1); }
    else { return (rd + 1); }
  }
}

void readInFile(FILE *f, Node leafarray[])
{
  int i = 0;
  char c;

  if(!f) {
    fprintf(stderr, "Could not read in file.\n");
    exit(1);
  }

  while((c = fgetc(f)) != EOF) {
    if(isalpha(c)) {
      c = toupper(c);
      for(i = 0; i < NUMCHARS; i++) {
        if((leafarray[i].character == c)) {
          leafarray[i].count++;
        }
      }
    }
  }
}

Node *buildLeafList(Node leafarray[])
{
  int i = 0;
  Node *head, *current;

  /*create first element in list (character initialised to EMPTYNODE)*/
  current = head = allocateNode();
  qsort(leafarray, NUMCHARS, sizeof(Node), countCompare);
  /*create a sorted linked list only from characters which appear in file*/
  for(i = 0; i < NUMCHARS; i++) {
    if(leafarray[i].count > 0) {
      /*if character is not set (i.e. 1 element in list)*/
      if(current->character == EMPTYNODE) {
        current->character = leafarray[i].character;
        current->count = leafarray[i].count;
      }
      else {
        current->next = allocateNode();
        current->next->character = leafarray[i].character;
        current->next->count = leafarray[i].count;
        current = current->next;
      }
    }
  }
  return head;
}

Node *buildHuffTree(Node *start)
{
  Node *parent;
  Node *current = start;

  /*if only one character in file*/
  if(!current->next) {
    return current;
  }

  while(current->next) {
    parent = createParent(current, current->next);
    sortedInsert(start, parent);
    current = current->next->next;
  }
  return parent;
}

void assignTreeArr(Node *tree, char **treearr, int x, int y)
{
  int maxdepth = 0;
  int yoffset = 0;
  int xoffset = 0;
  int n = 0;

  maxdepth = findTreeDepth(tree);
  if(tree->character == EMPTYNODE) { treearr[x][y] = '#'; }
  else { treearr[x][y] = tree->character; }

  if(tree->right) {
    yoffset = y + maxdepth;
    for(n = y+1; n < yoffset; n++) {
      treearr[x][n] = '-';
    }
    assignTreeArr(tree->right, treearr, x, yoffset);
  }
  if(tree->left) {
    xoffset = x + maxdepth;
    for(n = x+1; n < xoffset; n++) {
      treearr[n][y] = '|';
    }
    assignTreeArr(tree->left, treearr, xoffset, y);
  }
}

Node *createParent(Node *left, Node *right)
{
  Node *parent = allocateNode();
  parent->left = left;
  parent->right = right;
  parent->count = (left->count + right->count);
  return parent;
}

Node *allocateNode()
{
  Node *n = (Node *)malloc(sizeof(Node));

  if(!n) {
    fprintf(stderr, "Could not allocate Node.\n");
    exit(1);
  }

  n->character = EMPTYNODE;
  n->count = 0;
  n->left = NULL;
  n->right = NULL;
  n->next = NULL;
  return n;
}

/*function adapted from http://pleasemakeanote.blogspot.co.uk/2008/06/2d-arrays-in-c-using-malloc.html */
char **allocateTreeArr(int arrsize)
{
  int i = 0;
  char **treearr;

  /*calloc columns*/
  treearr = (char **)calloc(arrsize, sizeof(char *));
  if(!treearr) {
    fprintf(stderr, "Could not allocate tree array.\n");
    exit(1);
  }
  /*calloc rows*/
  for(i = 0; i < arrsize; i++) {
    *(treearr + i) = (char *)calloc(arrsize, sizeof(char));
    if(!*(treearr + i)) {
      fprintf(stderr, "Could not allocate tree array row %d.\n", i);
      exit(1);
    }
  }
  return treearr;
}

void freeList(Node *start)
{
  Node *temp;
  Node *n = start;

  while(n) {
    temp = n;
    n = n->next;
    free(temp);
  }
  start = NULL;
}

/*function adapted from http://pleasemakeanote.blogspot.co.uk/2008/06/2d-arrays-in-c-using-malloc.html */
void freeTreeArr(char **treearr, int arrsize)
{
   int i = 0;

   for(i = 0; i < arrsize; i++) {
     free(*(treearr + i));
   }
   free(treearr);
}

void printList(Node *current)
{
  while(current) {
    printf("%d: %d\n", current->character, current->count);
    current = current->next;
  }
}

void printArray(Node leafarray[])
{
  int i = 0;

  for(i = 0; i < NUMCHARS; i++) {
    printf("%3d: %d\n", leafarray[i].character, leafarray[i].count);
  }
}

void printTreeArr(char **treearr, int numnodes)
{
  int i, j = 0;
  for(i = 0; i < numnodes; i++) {
    for(j = 0; j < numnodes; j++) {
      printf("%c", treearr[i][j]);
    }
    printf("\n");
  }
}
