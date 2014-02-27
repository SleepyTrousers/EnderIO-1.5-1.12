package crazypants.vecmath;

import java.nio.FloatBuffer;

public class Matrix4d {

  public double m00 = 0;
  public double m01 = 0;
  public double m02 = 0;
  public double m03 = 0;
  public double m10 = 0;
  public double m11 = 0;
  public double m12 = 0;
  public double m13 = 0;
  public double m20 = 0;
  public double m21 = 0;
  public double m22 = 0;
  public double m23 = 0;
  public double m30 = 0;
  public double m31 = 0;
  public double m32 = 0;
  public double m33 = 0;

  public Matrix4d() {
  }

  public Matrix4d(float[] arr) {
    m00 = arr[0];
    m01 = arr[1];
    m02 = arr[2];
    m03 = arr[3];

    m10 = arr[4];
    m11 = arr[5];
    m12 = arr[6];
    m13 = arr[7];

    m20 = arr[8];
    m21 = arr[9];
    m22 = arr[10];
    m23 = arr[11];

    m30 = arr[12];
    m31 = arr[13];
    m32 = arr[14];
    m33 = arr[15];
  }

  public Matrix4d(double[] arr) {
    m00 = arr[0];
    m01 = arr[1];
    m02 = arr[2];
    m03 = arr[3];

    m10 = arr[4];
    m11 = arr[5];
    m12 = arr[6];
    m13 = arr[7];

    m20 = arr[8];
    m21 = arr[9];
    m22 = arr[10];
    m23 = arr[11];

    m30 = arr[12];
    m31 = arr[13];
    m32 = arr[14];
    m33 = arr[15];
  }

  public Matrix4d(Matrix4d other) {
    m00 = other.m00;
    m01 = other.m01;
    m02 = other.m02;
    m03 = other.m03;

    m10 = other.m10;
    m11 = other.m11;
    m12 = other.m12;
    m13 = other.m13;

    m20 = other.m20;
    m21 = other.m21;
    m22 = other.m22;
    m23 = other.m23;

    m30 = other.m30;
    m31 = other.m31;
    m32 = other.m32;
    m33 = other.m33;
  }

  public Matrix4d(double m00, double m01, double m02, double m03,
      double m10, double m11, double m12, double m13,
      double m20, double m21, double m22, double m23,
      double m30, double m31, double m32, double m33) {

    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    this.m03 = m03;

    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    this.m13 = m13;

    this.m20 = m20;
    this.m21 = m21;
    this.m22 = m22;
    this.m23 = m23;

    this.m30 = m30;
    this.m31 = m31;
    this.m32 = m32;
    this.m33 = m33;

  }

  public Matrix4d(FloatBuffer modelview) {
    m00 = modelview.get(0);
    m01 = modelview.get(1);
    m02 = modelview.get(2);
    m03 = modelview.get(3);

    m10 = modelview.get(4);
    m11 = modelview.get(5);
    m12 = modelview.get(6);
    m13 = modelview.get(7);

    m20 = modelview.get(8);
    m21 = modelview.get(9);
    m22 = modelview.get(10);
    m23 = modelview.get(11);

    m30 = modelview.get(12);
    m31 = modelview.get(13);
    m32 = modelview.get(14);
    m33 = modelview.get(15);
  }

  public final void setIdentity() {
    m00 = 1.0;
    m01 = 0.0;
    m02 = 0.0;
    m03 = 0.0;

    m10 = 0.0;
    m11 = 1.0;
    m12 = 0.0;
    m13 = 0.0;

    m20 = 0.0;
    m21 = 0.0;
    m22 = 1.0;
    m23 = 0.0;

    m30 = 0.0;
    m31 = 0.0;
    m32 = 0.0;
    m33 = 1.0;
  }

  public void set(Matrix4d other) {
    m00 = other.m00;
    m01 = other.m01;
    m02 = other.m02;
    m03 = other.m03;

    m10 = other.m10;
    m11 = other.m11;
    m12 = other.m12;
    m13 = other.m13;

    m20 = other.m20;
    m21 = other.m21;
    m22 = other.m22;
    m23 = other.m23;

    m30 = other.m30;
    m31 = other.m31;
    m32 = other.m32;
    m33 = other.m33;

  }

  public void set(double[] values) {
    m00 = values[0];
    m01 = values[1];
    m02 = values[2];
    m03 = values[3];
    m10 = values[4];
    m11 = values[5];
    m12 = values[6];
    m13 = values[7];
    m20 = values[8];
    m21 = values[9];
    m22 = values[10];
    m23 = values[11];
    m30 = values[12];
    m31 = values[13];
    m32 = values[14];
    m33 = values[15];
  }

  public double getElement(int row, int col) {
    switch (row) {
    case 0:
      switch (col) {
      case 0:
        return m00;
      case 1:
        return m01;
      case 2:
        return m02;
      case 3:
        return m03;
      default:
        break;
      }
      break;

    case 1:
      switch (col) {
      case 0:
        return m10;
      case 1:
        return m11;
      case 2:
        return m12;
      case 3:
        return m13;
      default:
        break;
      }
      break;

    case 2:
      switch (col) {
      case 0:
        return m20;
      case 1:
        return m21;
      case 2:
        return m22;
      case 3:
        return m23;
      default:
        break;
      }
      break;

    case 3:
      switch (col) {
      case 0:
        return m30;
      case 1:
        return m31;
      case 2:
        return m32;
      case 3:
        return m33;
      default:
        break;
      }
      break;

    default:
      break;
    }
    return 0;
  }

  public void setElement(int row, int column, double val) {
    switch (row) {

    case 0:
      switch (column) {
      case 0:
        m00 = val;
        break;
      case 1:
        m01 = val;
        break;
      case 2:
        m02 = val;
        break;
      case 3:
        m03 = val;
        break;
      default:
        break;
      }
      break;

    case 1:
      switch (column) {
      case 0:
        m10 = val;
        break;
      case 1:
        m11 = val;
        break;
      case 2:
        m12 = val;
        break;
      case 3:
        m13 = val;
        break;
      default:
        break;
      }
      break;

    case 2:
      switch (column) {
      case 0:
        m20 = val;
        break;
      case 1:
        m21 = val;
        break;
      case 2:
        m22 = val;
        break;
      case 3:
        m23 = val;
        break;
      default:
        break;
      }
      break;

    case 3:
      switch (column) {
      case 0:
        m30 = val;
        break;
      case 1:
        m31 = val;
        break;
      case 2:
        m32 = val;
        break;
      case 3:
        m33 = val;
        break;
      default:
        break;
      }
      break;

    default:
      break;
    }

  }

  public void getTranslation(Vector3d trans) {
    trans.x = m03;
    trans.y = m13;
    trans.z = m23;
  }

  public void setTranslation(Vector3d trans) {
    m03 = trans.x;
    m13 = trans.y;
    m23 = trans.z;
  }

  public void makeRotationX(double angle) {
    setIdentity();
    double sin = Math.sin(angle);
    double cos = Math.cos(angle);
    m11 = cos;
    m12 = -sin;
    m21 = sin;
    m22 = cos;
  }

  public void makeRotationY(double angle) {
    setIdentity();
    double sin = Math.sin(angle);
    double cos = Math.cos(angle);
    m00 = cos;
    m02 = sin;
    m20 = -sin;
    m22 = cos;
  }

  public void makeRotationZ(double angle) {
    setIdentity();
    double sin = Math.sin(angle);
    double cos = Math.cos(angle);
    m00 = cos;
    m01 = -sin;
    m10 = sin;
    m11 = cos;
  }

  public void transform(Vector3d vec) {
    double x = m00 * vec.x + m01 * vec.y + m02 * vec.z + m03;
    double y = m10 * vec.x + m11 * vec.y + m12 * vec.z + m13;
    vec.z = m20 * vec.x + m21 * vec.y + m22 * vec.z + m23;
    vec.x = x;
    vec.y = y;
  }

  public void transform(Vector4d vec) {
    double x = m00 * vec.x + m01 * vec.y + m02 * vec.z + m03 * vec.w;
    double y = m10 * vec.x + m11 * vec.y + m12 * vec.z + m13 * vec.w;
    double z = m20 * vec.x + m21 * vec.y + m22 * vec.z + m23 * vec.w;
    vec.w = m30 * vec.x + m31 * vec.y + m32 * vec.z + m33 * vec.w;
    vec.x = x;
    vec.y = y;
    vec.z = z;
  }

  public void transformNormal(Vector3f normal) {
    double x = m00 * normal.x + m01 * normal.y + m02 * normal.z;
    double y = m10 * normal.x + m11 * normal.y + m12 * normal.z;
    normal.z = (float) (m20 * normal.x + m21 * normal.y + m22 * normal.z);
    normal.x = (float) x;
    normal.y = (float) y;

  }

  public void transformNormal(Vector3d normal) {
    double x = m00 * normal.x + m01 * normal.y + m02 * normal.z;
    double y = m10 * normal.x + m11 * normal.y + m12 * normal.z;
    normal.z = m20 * normal.x + m21 * normal.y + m22 * normal.z;
    normal.x = x;
    normal.y = y;
  }

  public void mul(Matrix4d m1, Matrix4d m2) {
    set(m1);
    mul(m2);
  }

  public void mul(Matrix4d m) {
    double m00Tmp = m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30;
    double m01Tmp = m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31;
    double m02Tmp = m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32;
    double m03Tmp = m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33;

    double m10Tmp = m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30;
    double m11Tmp = m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31;
    double m12Tmp = m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32;
    double m13Tmp = m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33;

    double m20Tmp = m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30;
    double m21Tmp = m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31;
    double m22Tmp = m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32;
    double m23Tmp = m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33;

    double m30Tmp = m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30;
    double m31Tmp = m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31;
    double m32Tmp = m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32;
    double m33Tmp = m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33;

    m00 = m00Tmp;
    m01 = m01Tmp;
    m02 = m02Tmp;
    m03 = m03Tmp;
    m10 = m10Tmp;
    m11 = m11Tmp;
    m12 = m12Tmp;
    m13 = m13Tmp;
    m20 = m20Tmp;
    m21 = m21Tmp;
    m22 = m22Tmp;
    m23 = m23Tmp;
    m30 = m30Tmp;
    m31 = m31Tmp;
    m32 = m32Tmp;
    m33 = m33Tmp;
  }

  public void transpose() {
    double tmp = m10;
    m10 = m01;
    m01 = tmp;

    tmp = m20;
    m20 = m02;
    m02 = tmp;

    tmp = m30;
    m30 = m03;
    m03 = tmp;

    tmp = m21;
    m21 = m12;
    m12 = tmp;

    tmp = m31;
    m31 = m13;
    m13 = tmp;

    tmp = m32;
    m32 = m23;
    m23 = tmp;
  }

  public void invert() {
    double det = determinant();
    if(det == 0) {
      throw new RuntimeException("Cannot invert matrix with a determinat of 0.");
    }
    double detInv = 1f / det;

    // first row
    double t00 = determinant3x3(m11, m21, m31, m12, m22, m32, m13, m23, m33);
    double t01 = -determinant3x3(m01, m21, m31, m02, m22, m32, m03, m23, m33);
    double t02 = determinant3x3(m01, m11, m31, m02, m12, m32, m03, m13, m33);
    double t03 = -determinant3x3(m01, m11, m21, m02, m12, m22, m03, m13, m23);
    // second row
    double t10 = -determinant3x3(m10, m20, m30, m12, m22, m32, m13, m23, m33);
    double t11 = determinant3x3(m00, m20, m30, m02, m22, m32, m03, m23, m33);
    double t12 = -determinant3x3(m00, m10, m30, m02, m12, m32, m03, m13, m33);
    double t13 = determinant3x3(m00, m10, m20, m02, m12, m22, m03, m13, m23);
    // third row
    double t20 = determinant3x3(m10, m20, m30, m11, m21, m31, m13, m23, m33);
    double t21 = -determinant3x3(m00, m20, m30, m01, m21, m31, m03, m23, m33);
    double t22 = determinant3x3(m00, m10, m30, m01, m11, m31, m03, m13, m33);
    double t23 = -determinant3x3(m00, m10, m20, m01, m11, m21, m03, m13, m23);
    // fourth row
    double t30 = -determinant3x3(m10, m20, m30, m11, m21, m31, m12, m22, m32);
    double t31 = determinant3x3(m00, m20, m30, m01, m21, m31, m02, m22, m32);
    double t32 = -determinant3x3(m00, m10, m30, m01, m11, m31, m02, m12, m32);
    double t33 = determinant3x3(m00, m10, m20, m01, m11, m21, m02, m12, m22);

    m00 = t00 * detInv;
    m11 = t11 * detInv;
    m22 = t22 * detInv;
    m33 = t33 * detInv;
    m10 = t10 * detInv;
    m01 = t01 * detInv;
    m02 = t02 * detInv;
    m20 = t20 * detInv;
    m21 = t21 * detInv;
    m12 = t12 * detInv;
    m30 = t30 * detInv;
    m03 = t03 * detInv;
    m31 = t31 * detInv;
    m13 = t13 * detInv;
    m23 = t23 * detInv;
    m32 = t32 * detInv;

  }

  public double determinant() {
    double result =
        m00
            * ((m11 * m22 * m33 + m21 * m32 * m13 + m31 * m12 * m23)
                - m31 * m22 * m13
                - m11 * m32 * m23
                - m21 * m12 * m33);
    result -= m10
        * ((m01 * m22 * m33 + m21 * m32 * m03 + m31 * m02 * m23)
            - m31 * m22 * m03
            - m01 * m32 * m23
            - m21 * m02 * m33);
    result += m20
        * ((m01 * m12 * m33 + m11 * m32 * m03 + m31 * m02 * m13)
            - m31 * m12 * m03
            - m01 * m32 * m13
            - m11 * m02 * m33);
    result -= m30
        * ((m01 * m12 * m23 + m11 * m22 * m03 + m21 * m02 * m13)
            - m21 * m12 * m03
            - m01 * m22 * m13
            - m11 * m02 * m23);
    return result;
  }

  private double determinant3x3(double e00, double e01, double e02, double e10, double e11, double e12, double e20, double e21, double e22) {
    return e00 * (e11 * e22 - e12 * e21) + e01 * (e12 * e20 - e10 * e22) + e02 * (e10 * e21 - e11 * e20);
  }

  @Override
  public String toString() {
    return "Matrix4d(\n" +
        "  " + m00 + ", " + m01 + ", " + m02 + ", " + m03 + "\n" +
        "  " + m10 + ", " + m11 + ", " + m12 + ", " + m13 + "\n" +
        "  " + m20 + ", " + m21 + ", " + m22 + ", " + m23 + "\n" +
        "  " + m30 + ", " + m31 + ", " + m32 + ", " + m33 + "\n" +
        ")\n";
  }
}
