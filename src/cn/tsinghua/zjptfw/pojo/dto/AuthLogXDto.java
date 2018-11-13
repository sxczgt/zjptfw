package cn.tsinghua.zjptfw.pojo.dto;

/**
 * @Auther: guotao
 * @Date: 2018/11/12 17:27
 * @Description:
 */
public class AuthLogXDto {

    private long id;

    private String opDesc;

    private String opSystem;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOpDesc() {
        return opDesc;
    }

    public void setOpDesc(String opDesc) {
        this.opDesc = opDesc;
    }

    public String getOpSystem() {
        return opSystem;
    }

    public void setOpSystem(String opSystem) {
        this.opSystem = opSystem;
    }

    @Override
    public String toString() {
        return "AuthLogXDto{" +
                "id=" + id +
                ", opDesc='" + opDesc + '\'' +
                ", opSystem='" + opSystem + '\'' +
                '}';
    }
}
