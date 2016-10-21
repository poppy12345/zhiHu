package cn.nju.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;

/**
 * Created by 黄锐鸿 on 2016/10/19.
 */
@Service
public class SensitiveService implements InitializingBean{
    private static final Logger logger= LoggerFactory.getLogger(SensitiveService.class);

    @Override
    public void afterPropertiesSet() throws Exception {

        InputStream is=null;
        InputStreamReader read=null;
        BufferedReader bufferedReader=null;
        try {
            //is=new FileInputStream(new File("/src/main/resources/SensitiveWord.txt"));
            is=Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWord.txt");
            read=new InputStreamReader(is);
            bufferedReader=new BufferedReader(read);
            String word=null;
            while((word=bufferedReader.readLine())!=null){
                addWord(word.trim());
            }

        }catch (Exception e){
            logger.error("读取敏感词文件失败！");
        }finally {
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(read!=null){
                read.close();
            }

            if(is!=null){
                is.close();
            }
        }
    }

    private void addWord(String lineTxt){
        TrieNode tmpNode=root;
        for(int i=0;i<lineTxt.length();i++){
            Character c=lineTxt.charAt(i);
            TrieNode node=tmpNode.getSubNode(c);
            if(node==null){
                node=new TrieNode();
                tmpNode.addSubNode(c,node);
            }
            tmpNode=node;

            if(i==lineTxt.length()-1){
                tmpNode.setKeyWordEnd(true);
            }
        }
    }

    private class TrieNode{
        //是不是关键词结尾
        private boolean end=false;

        //存放子结点，key是字符，value是对应的TrieNode
        private HashMap<Character,TrieNode> subNodes=new HashMap<>();

        public void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }

        public TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        public boolean isKeyWordEnd(){
            return end;
        }

        public void setKeyWordEnd(boolean end){
            this.end=end;
        }

        public int getSubNodeCount(){
            return subNodes.size();
        }

    }

    private boolean isSymbol(char c){
        int ic=(int) c;
        //0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c)&&(ic<0x2E80||ic>0x9FFF);
    }

    private TrieNode root=new TrieNode();

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        String replacement="***";
        StringBuilder result=new StringBuilder();
        TrieNode tmpNode=root;
        int begin=0;
        int position=0;

        while(position<text.length()){
            char c=text.charAt(position);
            if(isSymbol(c)){
                if(tmpNode==root){
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tmpNode=tmpNode.getSubNode(c);


            if(tmpNode==null){  //以begin开始的字符串不存在

                result.append(text.charAt(begin));
                position=begin+1;
                begin=position;
                tmpNode=root;
            }else if(tmpNode.isKeyWordEnd()){
                result.append(replacement);
                position=position+1;
                begin=position;
                tmpNode=root;
            }else{
                position++;
            }
        }

        return result.toString();
    }

    public static void main(String[] args){
        String text="这#是#色#情";
        SensitiveService s=new SensitiveService();
        s.addWord("色情");
        System.out.println(s.filter(text));
    }
}
