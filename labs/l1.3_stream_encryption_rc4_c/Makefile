CC = g++

TARGET_EXEC ?= result

BUILD_DIR ?= ./build
SRC_DIRS ?= /

SRCS := a.cpp
OBJS := $(SRCS:%=$(BUILD_DIR)/%.o)
DEPS := $(OBJS:.o=.d)

CFLAGS ?=

$(BUILD_DIR)/$(TARGET_EXEC): $(SRCS)
	$(MKDIR_P) $(BUILD_DIR)
	$(CC) $(CFLAGS) $(SRCS) -o $@

.PHONY: all clean run mem pack

all: $(BUILD_DIR)/$(TARGET_EXEC)

clean:
	$(RM) -r $(BUILD_DIR)
	$(RM) pipes.log events.log
	$(RM) pa1.tar.gz

run: $(BUILD_DIR)/$(TARGET_EXEC)
	$(BUILD_DIR)/$(TARGET_EXEC)
	
mem_child: $(BUILD_DIR)/$(TARGET_EXEC)
	valgrind --tool=memcheck -s --track-origins=yes $(BUILD_DIR)/$(TARGET_EXEC) -p $(ARGS)

mem: $(BUILD_DIR)/$(TARGET_EXEC)
	valgrind --tool=memcheck --child-silent-after-fork=yes -s --track-origins=yes $(BUILD_DIR)/$(TARGET_EXEC) -p $(ARGS)

pack: clean
	mkdir ~/pa1
	cp -r * ~/pa1
	# tar czf ~/pa1.tar.gz ~/pa1

MKDIR_P ?= mkdir -p
