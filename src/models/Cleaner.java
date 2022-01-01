        int sum = 0;
    Queue<String> queue = new LinkedList<String>();

    int solution(int[][] office, int r, int c, String[] move) {
        for (String s : move) {
            queue.offer(s);
        }

        int[][] dirs = { { 0, 1 }, { 1, 0 }, { -1, 0 }, { 0, -1 } };
        if (r < 0 || c < 0 || r >= office.length || c >= office[0].length) {
            return -1;
        }
        if (office[r][c] != -1) {
            sum = sum + office[r][c];
        }
        int ind = 1;
        int[] dir = dirs[1];
        /*
         * queue.poll();
         * sum = sum + office[r][c];
         * office[r][c] = 0;
         */
        cleaner(office, r, c, dirs, dir, ind);
        return sum;
    }





void cleaner(int[][] office, int r, int c, int[][] dirs, int[] dir, int ind) {
        if (queue.size() == 0) {
            return;	
        }
        String fmove = queue.poll();
        if (fmove.equals("go")) {
            // check if there's something
            if (office[r + dir[0]][c + dir[1]] == -1 || r + dir[0] < 0 || c + dir[1] < 0 || r + dir[0] >= office.length
                    || c + dir[1] >= office[0].length) {
                queue.poll();
                office[r][c] = 0;
                cleaner(office, r, c, dirs, dir, ind);
                return;
            }
            r = r + dir[0];
            c = c + dir[1];
            queue.poll();
            office[r][c] = 0;
            sum = sum + office[r][c];
            cleaner(office, r, c, dirs, dir, ind);
            return;
        } else if (fmove.equals("left")) {
            if (ind - 1 < 0) {
                ind = 3;
            } else {
                ind--;
            }
            dir = dirs[ind];
            queue.poll();
            sum = sum + office[r][c];
            office[r][c] = 0;
            cleaner(office, r, c, dirs, dir, ind);
            return;
        } else if (fmove.equals("right")) {
            if (ind + 1 > 3) {
                ind = 0;
            } else {
                ind++;
            }
            ddir = dirs[ind];
            queue.poll();
            sum = sum + office[r][c];
            cleaner(office, r, c, dirs, dir, ind);
            return;
        }
        return;
    }