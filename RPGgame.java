import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Random;

public class RPGgame {
    public static void main(String[] args) {
        Login loginWindow = new Login();
        loginWindow.showLoginWindow();
    }
}

class Login {
    private final String defaultUsername = "Admin";
    private final String defaultPassword = "0000";

    public void showLoginWindow() {
        JFrame frame = new JFrame("RPG 登入系統");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel userLabel = new JLabel("帳號:");
        userLabel.setBounds(50, 30, 80, 30);
        frame.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(150, 30, 150, 30);
        frame.add(userField);

        JLabel passLabel = new JLabel("密碼:");
        passLabel.setBounds(50, 70, 80, 30);
        frame.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 70, 150, 30);
        frame.add(passField);

        JButton loginButton = new JButton("登入");
        loginButton.setBounds(150, 110, 80, 30);
        frame.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.equals(defaultUsername) && password.equals(defaultPassword)) {
                JOptionPane.showMessageDialog(frame, "登入成功！");
                frame.dispose();
                new Game().startGame();
            } else {
                JOptionPane.showMessageDialog(frame, "帳號或密碼錯誤！", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}

class Game {
    private Character character;
    private Monster monster;
    private JLabel playerInfo;
    private JProgressBar expBar;
    private JLabel monsterLabel;
    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 800;
    private Random random = new Random();

    public void startGame() {
        JFrame gameFrame = new JFrame("game");
        gameFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLayout(null);

        character = new Character("玩家.png", WINDOW_WIDTH, WINDOW_HEIGHT);
        JLabel characterLabel = character.getCharacterLabel();
        gameFrame.add(characterLabel);

        monster = new Monster("怪物.png", randomPosition(WINDOW_WIDTH), randomPosition(WINDOW_HEIGHT));
        monsterLabel = monster.getMonsterLabel();
        gameFrame.add(monsterLabel);

        playerInfo = new JLabel(character.getInfo());
        playerInfo.setBounds(10, WINDOW_HEIGHT - 60, WINDOW_WIDTH, 30);
        gameFrame.add(playerInfo);

        expBar = new JProgressBar(0, 100);
        expBar.setBounds(10, WINDOW_HEIGHT - 30, WINDOW_WIDTH - 20, 20);
        expBar.setStringPainted(true);
        gameFrame.add(expBar);

        gameFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                character.move(keyCode);
                playerInfo.setText(character.getInfo());
                checkCollision(gameFrame);
            }
        });

        gameFrame.setFocusable(true);
        gameFrame.requestFocusInWindow();
        gameFrame.setVisible(true);
    }

    private void checkCollision(JFrame frame) {
        if (character.getBounds().intersects(monster.getBounds())) {
            while (true) {
                int choice = JOptionPane.showOptionDialog(frame,
                        "遇到怪物！選擇行動:\n玩家: " + character.getInfo() + "\n怪物: " + monster.getInfo(),
                        "戰鬥",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[]{"攻擊", "魔法", "逃跑"},
                        "攻擊");

                if (choice == 0) { // 攻擊
                    monster.takeDamage(character.getAttack());
                    if (monster.isDefeated()) {
                        JOptionPane.showMessageDialog(frame, "怪物被擊敗！");
                        character.gainExp(20);
                        expBar.setValue(character.getExp());
                        playerInfo.setText(character.getInfo());
                        respawnMonster(frame);
                        break;
                    } else {
                        monster.attack(character);
                        if (character.isDefeated()) {
                            JOptionPane.showMessageDialog(frame, "你被怪物擊敗了！遊戲結束。");
                            System.exit(0);
                        }
                    }
                } else if (choice == 1) { // 魔法
                    monster.takeDamage(character.castMagic());
                    if (monster.isDefeated()) {
                        JOptionPane.showMessageDialog(frame, "怪物被擊敗！");
                        character.gainExp(20);
                        expBar.setValue(character.getExp());
                        playerInfo.setText(character.getInfo());
                        respawnMonster(frame);
                        break;
                    } else {
                        monster.attack(character);
                        if (character.isDefeated()) {
                            JOptionPane.showMessageDialog(frame, "你被怪物擊敗了！遊戲結束。");
                            System.exit(0);
                        }
                    }
                } else if (choice == 2) { // 逃跑
                    JOptionPane.showMessageDialog(frame, "你逃跑了！");
                    break;
                }
                playerInfo.setText(character.getInfo());
            }
        }
    }

    private void respawnMonster(JFrame frame) {
        int newX = randomPosition(WINDOW_WIDTH);
        int newY = randomPosition(WINDOW_HEIGHT - 150);
        monster.respawn(newX, newY);
        monsterLabel.setLocation(monster.getX(), monster.getY());
        monsterLabel.setVisible(true);
        frame.repaint();
    }

    private int randomPosition(int limit) {
        return random.nextInt(limit - 100) + 50;
    }
}

class Character {
    private int x = 375, y = 275;
    private int level = 1, hp = 100, mp = 50, attack = 10, exp = 0;
    private final JLabel characterLabel;
    private final int windowWidth, windowHeight;

    public Character(String imagePath, int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        characterLabel = new JLabel(icon);
        characterLabel.setBounds(x, y, 50, 50);
    }

    public void move(int keyCode) {
        if (keyCode == KeyEvent.VK_UP && y > 0) y -= 10;
        if (keyCode == KeyEvent.VK_DOWN && y < windowHeight - 110) y += 10;
        if (keyCode == KeyEvent.VK_LEFT && x > 0) x -= 10;
        if (keyCode == KeyEvent.VK_RIGHT && x < windowWidth - 70) x += 10;
        characterLabel.setLocation(x, y);
    }

    public int getAttack() {
        return attack;
    }

    public int castMagic() {
        if (mp >= 10) {
            mp -= 10;
            return attack * 2;
        }
        return 0;
    }

    public void gainExp(int amount) {
        exp += amount;
        if (exp >= 100) {
            levelUp();
            exp = 0;
        }
    }

    public int getExp() {
        return exp;
    }

    private void levelUp() {
        level++;
        hp = 100;
        mp = 50;
        attack += 5;
        JOptionPane.showMessageDialog(null, "升級了！等級: " + level + " | 狀態已回滿！");
    }

    public Rectangle getBounds() {
        return characterLabel.getBounds();
    }

    public String getInfo() {
        return "角色: 小八 | 等級: " + level + " | HP: " + hp + " | MP: " + mp + " | 攻擊力: " + attack;
    }

    public JLabel getCharacterLabel() {
        return characterLabel;
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    public boolean isDefeated() {
        return hp <= 0;
    }
}

class Monster {
    private int hp = 50, level = 1, attack = 5;
    private int x, y;
    private final JLabel monsterLabel;

    public Monster(String imagePath, int x, int y) {
        this.x = x;
        this.y = y;
        ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        monsterLabel = new JLabel(icon);
        monsterLabel.setBounds(x, y, 50, 50);
    }

    public Rectangle getBounds() {
        return monsterLabel.getBounds();
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    public void attack(Character character) {
        character.takeDamage(attack);
    }

    public boolean isDefeated() {
        return hp <= 0;
    }

    public void respawn(int x, int y) {
        this.x = x;
        this.y = y;
        hp = 50;
        monsterLabel.setLocation(x, y);
        monsterLabel.setVisible(true);
    }

    public String getInfo() {
        return "名稱: 哥布林 | 等級: " + level + " | HP: " + hp + " | 攻擊力: " + attack;
    }

    public JLabel getMonsterLabel() {
        return monsterLabel;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
